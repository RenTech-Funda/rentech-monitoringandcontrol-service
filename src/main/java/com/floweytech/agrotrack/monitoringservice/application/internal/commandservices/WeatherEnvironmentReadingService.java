package com.floweytech.agrotrack.monitoringservice.application.internal.commandservices;

import com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices.ExternalPlotService;
import com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices.OpenWeatherService;
import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.EnvironmentReading;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreateEnvironmentReadingCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingValue;
import com.floweytech.agrotrack.monitoringservice.domain.services.EnvironmentReadingCommandService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherEnvironmentReadingService {

    private final ExternalPlotService externalPlotService;
    private final OpenWeatherService openWeatherService;
    private final EnvironmentReadingCommandService environmentReadingCommandService;

    public WeatherEnvironmentReadingService(
            ExternalPlotService externalPlotService,
            OpenWeatherService openWeatherService,
            EnvironmentReadingCommandService environmentReadingCommandService) {
        this.externalPlotService = externalPlotService;
        this.openWeatherService = openWeatherService;
        this.environmentReadingCommandService = environmentReadingCommandService;
    }

    public List<EnvironmentReading> importCurrentWeatherForPlot(Long plotId) {
        var plot = externalPlotService.getPlotById(plotId);
        if (plot == null) {
            throw new IllegalArgumentException("Plot not found: " + plotId);
        }

        var weather = openWeatherService.getCurrentWeatherByLocation(plot.location());
        if (weather == null || weather.main() == null) {
            throw new IllegalStateException("OpenWeather returned no weather data for plot location: " + plot.location());
        }

        var measuredAt = weather.dt() == null
                ? LocalDateTime.now(ZoneOffset.UTC)
                : LocalDateTime.ofInstant(Instant.ofEpochSecond(weather.dt()), ZoneOffset.UTC);

        var readings = new ArrayList<EnvironmentReading>();

        if (weather.main().temp() != null) {
            createReading(plotId, ReadingType.TEMPERATURE, weather.main().temp(), "C", measuredAt)
                    .ifPresent(readings::add);
        }

        if (weather.main().humidity() != null) {
            createReading(plotId, ReadingType.HUMIDITY, weather.main().humidity().doubleValue(), "%", measuredAt)
                    .ifPresent(readings::add);
        }

        if (readings.isEmpty()) {
            throw new IllegalStateException("OpenWeather did not return supported readings for plot location: " + plot.location());
        }

        return readings;
    }

    private java.util.Optional<EnvironmentReading> createReading(
            Long plotId,
            ReadingType type,
            Double value,
            String unit,
            LocalDateTime measuredAt) {
        return environmentReadingCommandService.handle(new CreateEnvironmentReadingCommand(
                new PlotId(plotId),
                type,
                new ReadingValue(value, unit),
                measuredAt
        ));
    }
}
