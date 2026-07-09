package com.floweytech.agrotrack.monitoringservice.application.internal.insights;

import com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices.ExternalPlotService;
import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.EnvironmentReading;
import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.TaskStatus;
import com.floweytech.agrotrack.monitoringservice.infrastructure.ai.AiTextClient;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.EnvironmentReadingRepository;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantSamplingSessionRepository;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.TaskRepository;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class PlotInsightService {

    private final ExternalPlotService externalPlotService;
    private final EnvironmentReadingRepository environmentReadingRepository;
    private final PlantSamplingSessionRepository plantSamplingSessionRepository;
    private final TaskRepository taskRepository;
    private final AiTextClient aiTextClient;

    public PlotInsightService(
            ExternalPlotService externalPlotService,
            EnvironmentReadingRepository environmentReadingRepository,
            PlantSamplingSessionRepository plantSamplingSessionRepository,
            TaskRepository taskRepository,
            AiTextClient aiTextClient) {
        this.externalPlotService = externalPlotService;
        this.environmentReadingRepository = environmentReadingRepository;
        this.plantSamplingSessionRepository = plantSamplingSessionRepository;
        this.taskRepository = taskRepository;
        this.aiTextClient = aiTextClient;
    }

    public PlotHealthSummaryResource getHealthSummary(Long plotId) {
        var context = buildContext(plotId);
        var alerts = evaluateAlerts(context);
        var mainProblems = alerts.stream().map(AlertResource::title).toList();
        int score = calculateHealthScore(alerts, context);
        var status = score >= 80 ? "GOOD" : score >= 60 ? "WARNING" : "CRITICAL";
        var fallbackSummary = buildHealthSummary(context, score, status, mainProblems);
        var summary = aiTextClient.summarizeHealth(context.toPrompt(), fallbackSummary);

        return new PlotHealthSummaryResource(
                context.plot().id(),
                context.plot().plotName(),
                context.plot().organizationId(),
                score,
                status,
                summary,
                mainProblems,
                alerts
        );
    }

    public PlotRecommendationsResource getRecommendations(Long plotId) {
        var context = buildContext(plotId);
        var recommendations = new ArrayList<RecommendationResource>();
        var suggestedTasks = new ArrayList<SuggestedTaskResource>();

        if (context.avgHumidity().isPresent() && context.avgHumidity().getAsDouble() > 85) {
            recommendations.add(new RecommendationResource(
                    "HUMIDITY_CONTROL",
                    "MEDIUM",
                    "Revisar drenaje o ventilación: la humedad promedio está elevada."
            ));
            suggestedTasks.add(new SuggestedTaskResource(
                    "Revisar drenaje de parcela",
                    "La humedad promedio supera el rango recomendado y puede favorecer enfermedades foliares.",
                    "MEDIUM"
            ));
        }

        if (context.avgPh().isPresent() && (context.avgPh().getAsDouble() < 5.5 || context.avgPh().getAsDouble() > 7.5)) {
            recommendations.add(new RecommendationResource(
                    "PH_CONTROL",
                    "HIGH",
                    "Repetir medición de pH y evaluar corrección del suelo antes de aplicar tratamientos."
            ));
            suggestedTasks.add(new SuggestedTaskResource(
                    "Verificar pH del suelo",
                    "El pH promedio está fuera del rango general recomendado para varios cultivos.",
                    "HIGH"
            ));
        }

        if (context.avgTemperature().isPresent() && context.avgTemperature().getAsDouble() > 32) {
            recommendations.add(new RecommendationResource(
                    "HEAT_STRESS_PREVENTION",
                    "MEDIUM",
                    "Evitar labores en horas de mayor calor y revisar disponibilidad de riego."
            ));
        }

        if (context.lowGrowth()) {
            recommendations.add(new RecommendationResource(
                    "LOW_GROWTH_FOLLOW_UP",
                    "MEDIUM",
                    "El último muestreo muestra crecimiento bajo; conviene revisar riego, nutrientes o plagas."
            ));
            suggestedTasks.add(new SuggestedTaskResource(
                    "Inspeccionar crecimiento de plantas",
                    "Comparar visualmente plantas representativas y registrar un nuevo muestreo.",
                    "MEDIUM"
            ));
        }

        if (recommendations.isEmpty()) {
            recommendations.add(new RecommendationResource(
                    "KEEP_MONITORING",
                    "LOW",
                    "No se detectan riesgos fuertes con los datos actuales. Mantener monitoreo periódico."
            ));
        }

        var riskLevel = recommendations.stream().anyMatch(r -> r.priority().equals("HIGH"))
                ? "HIGH"
                : recommendations.stream().anyMatch(r -> r.priority().equals("MEDIUM")) ? "MEDIUM" : "LOW";

        var fallbackSummary = "La parcela " + context.plot().plotName() + " presenta riesgo " + riskLevel
                + " según las lecturas y muestreos disponibles.";
        var summary = aiTextClient.summarizeRecommendations(context.toPrompt(), recommendations, fallbackSummary);

        return new PlotRecommendationsResource(
                context.plot().id(),
                context.plot().plotName(),
                riskLevel,
                summary,
                recommendations,
                suggestedTasks
        );
    }

    private PlotInsightContext buildContext(Long plotId) {
        var plot = externalPlotService.getPlotById(plotId);
        if (plot == null) {
            throw new IllegalArgumentException("Plot not found: " + plotId);
        }

        var readings = environmentReadingRepository.findAllByPlotId(new PlotId(plotId));
        var sessions = plantSamplingSessionRepository.findAllByPlotId(new PlotId(plotId)).stream()
                .sorted(Comparator.comparing(PlantSamplingSession::getSampledAt))
                .toList();
        var organizationTasks = taskRepository.findByOrganizationId_OrganizationId(plot.organizationId());

        var overdueTasks = organizationTasks.stream()
                .filter(task -> task.getTaskStatus() != TaskStatus.COMPLETED && task.getTaskStatus() != TaskStatus.CANCELLED)
                .filter(task -> task.getDateRange().getEndDate().isBefore(LocalDate.now()))
                .count();

        return new PlotInsightContext(
                plot,
                average(readings, ReadingType.TEMPERATURE),
                average(readings, ReadingType.HUMIDITY),
                average(readings, ReadingType.PH_LEVEL),
                latestReadingAt(readings),
                lowGrowth(sessions),
                overdueTasks
        );
    }

    private List<AlertResource> evaluateAlerts(PlotInsightContext context) {
        var alerts = new ArrayList<AlertResource>();

        context.avgTemperature().ifPresent(value -> {
            if (value > 32) {
                alerts.add(new AlertResource(
                        "HIGH_TEMPERATURE",
                        "WARNING",
                        "Temperatura elevada",
                        "La temperatura promedio supera 32 °C."
                ));
            }
        });

        context.avgHumidity().ifPresent(value -> {
            if (value > 85) {
                alerts.add(new AlertResource(
                        "HIGH_HUMIDITY",
                        "WARNING",
                        "Humedad elevada",
                        "La humedad promedio supera 85%."
                ));
            }
        });

        context.avgPh().ifPresent(value -> {
            if (value < 5.5 || value > 7.5) {
                alerts.add(new AlertResource(
                        "PH_OUT_OF_RANGE",
                        "CRITICAL",
                        "pH fuera de rango",
                        "El pH promedio está fuera del rango general recomendado."
                ));
            }
        });

        if (context.latestReadingAt() == null || context.latestReadingAt().isBefore(LocalDateTime.now().minusDays(7))) {
            alerts.add(new AlertResource(
                    "NO_RECENT_READINGS",
                    "INFO",
                    "Sin lecturas recientes",
                    "No hay lecturas ambientales recientes para esta parcela."
            ));
        }

        if (context.lowGrowth()) {
            alerts.add(new AlertResource(
                    "LOW_PLANT_GROWTH",
                    "WARNING",
                    "Crecimiento bajo",
                    "El último muestreo indica crecimiento bajo frente al muestreo anterior."
            ));
        }

        if (context.overdueTasks() > 0) {
            alerts.add(new AlertResource(
                    "OVERDUE_TASKS",
                    "WARNING",
                    "Tareas vencidas",
                    "Hay " + context.overdueTasks() + " tarea(s) vencida(s) en la organización."
            ));
        }

        return alerts;
    }

    private int calculateHealthScore(List<AlertResource> alerts, PlotInsightContext context) {
        int score = 100;
        for (var alert : alerts) {
            score -= switch (alert.severity()) {
                case "CRITICAL" -> 25;
                case "WARNING" -> 15;
                default -> 5;
            };
        }
        return Math.max(score, 0);
    }

    private String buildHealthSummary(PlotInsightContext context, int score, String status, List<String> mainProblems) {
        if (mainProblems.isEmpty()) {
            return "La parcela " + context.plot().plotName() + " se encuentra estable con un score de " + score + "/100.";
        }
        return "La parcela " + context.plot().plotName() + " tiene estado " + status + " con score "
                + score + "/100. Principales puntos de atención: " + String.join(", ", mainProblems) + ".";
    }

    private OptionalDouble average(List<EnvironmentReading> readings, ReadingType type) {
        return readings.stream()
                .filter(reading -> reading.getType() == type)
                .mapToDouble(reading -> reading.getReadingValue().getValue())
                .average();
    }

    private LocalDateTime latestReadingAt(List<EnvironmentReading> readings) {
        return readings.stream()
                .map(EnvironmentReading::getMeasuredAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private boolean lowGrowth(List<PlantSamplingSession> sessions) {
        if (sessions.size() < 2) return false;
        var previous = sessions.get(sessions.size() - 2).getAverage().getAvgHeightCm();
        var current = sessions.getLast().getAverage().getAvgHeightCm();
        if (previous == null || previous <= 0 || current == null) return false;
        var changePercent = ((current - previous) / previous) * 100;
        return changePercent < 2;
    }

    private record PlotInsightContext(
            com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices.PlotResource plot,
            OptionalDouble avgTemperature,
            OptionalDouble avgHumidity,
            OptionalDouble avgPh,
            LocalDateTime latestReadingAt,
            boolean lowGrowth,
            long overdueTasks
    ) {
        String toPrompt() {
            return """
                    Parcela: %s
                    Ubicación: %s
                    Organización: %d
                    Temperatura promedio: %s
                    Humedad promedio: %s
                    pH promedio: %s
                    Última lectura: %s
                    Crecimiento bajo: %s
                    Tareas vencidas: %d
                    """.formatted(
                    plot.plotName(),
                    plot.location(),
                    plot.organizationId(),
                    avgTemperature.isPresent() ? avgTemperature.getAsDouble() : "sin datos",
                    avgHumidity.isPresent() ? avgHumidity.getAsDouble() : "sin datos",
                    avgPh.isPresent() ? avgPh.getAsDouble() : "sin datos",
                    latestReadingAt,
                    lowGrowth,
                    overdueTasks
            );
        }
    }
}
