package com.floweytech.agrotrack.monitoringservice.interfaces.rest.controllers;

import com.floweytech.agrotrack.monitoringservice.application.internal.commandservices.WeatherEnvironmentReadingService;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsByPlotIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetEnvironmentReadingByIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetEnvironmentReadingsByPlotIdAndTypeAndPeriodQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;
import com.floweytech.agrotrack.monitoringservice.domain.services.EnvironmentReadingCommandService;
import com.floweytech.agrotrack.monitoringservice.domain.services.EnvironmentReadingQueryService;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.CreateEnvironmentReadingResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.EnvironmentReadingResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.ReadingResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform.CreateEnvironmentReadingCommandFromResourceAssembler;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform.EnvironmentReadingResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/environment-readings", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Environment Readings", description = "Environment Readings Management Endpoints")
public class EnvironmentReadingsController {

    private final EnvironmentReadingCommandService environmentReadingCommandService;
    private final EnvironmentReadingQueryService environmentReadingQueryService;
    private final WeatherEnvironmentReadingService weatherEnvironmentReadingService;

    public EnvironmentReadingsController(EnvironmentReadingCommandService environmentReadingCommandService,
                                         EnvironmentReadingQueryService environmentReadingQueryService,
                                         WeatherEnvironmentReadingService weatherEnvironmentReadingService) {
        this.environmentReadingCommandService = environmentReadingCommandService;
        this.environmentReadingQueryService = environmentReadingQueryService;
        this.weatherEnvironmentReadingService = weatherEnvironmentReadingService;
    }

    @PostMapping
    @Operation(summary = "Create a new environment reading")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Environment reading created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input data")
    })
    public ResponseEntity<EnvironmentReadingResource> createEnvironmentReading(
            @RequestBody CreateEnvironmentReadingResource resource) {

        var command = CreateEnvironmentReadingCommandFromResourceAssembler.toCommandFromResource(resource);
        var environmentReading = environmentReadingCommandService.handle(command);

        if (environmentReading.isEmpty())
            return ResponseEntity.badRequest().build();

        var resource2 = EnvironmentReadingResourceFromEntityAssembler.toResourceFromEntity(environmentReading.get());
        return new ResponseEntity<>(resource2, HttpStatus.CREATED);
    }

    @PostMapping("/weather/plot/{plotId}")
    @Operation(summary = "Import current weather readings for a plot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Weather readings imported successfully"),
            @ApiResponse(responseCode = "400", description = "Plot location could not be resolved or weather data is invalid"),
            @ApiResponse(responseCode = "503", description = "External weather service is temporarily unavailable")
    })
    public ResponseEntity<List<EnvironmentReadingResource>> importCurrentWeatherForPlot(@PathVariable Long plotId) {
        var readings = weatherEnvironmentReadingService.importCurrentWeatherForPlot(plotId);
        var resources = readings.stream()
                .map(EnvironmentReadingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(resources);
    }

    @GetMapping
    @Operation(summary = "Get all environment readings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of environment readings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No environment readings found")
    })
    public ResponseEntity<List<EnvironmentReadingResource>> getAllEnvironmentReadings() {
        var readings = environmentReadingQueryService.handle(new GetAllEnvironmentReadingsQuery());
        if (readings.isEmpty()) return ResponseEntity.notFound().build();
        var resources = readings.stream()
                .map(EnvironmentReadingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{environmentReadingId}")
    @Operation(summary = "Get an environment reading by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Environment reading retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Environment reading not found")
    })
    public ResponseEntity<EnvironmentReadingResource> getEnvironmentReadingById(
            @PathVariable Long environmentReadingId) {
        var reading = environmentReadingQueryService.handle(new GetEnvironmentReadingByIdQuery(environmentReadingId));
        if (reading.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(EnvironmentReadingResourceFromEntityAssembler.toResourceFromEntity(reading.get()));
    }

    @GetMapping("/plot/{plotId}")
    @Operation(summary = "Get environment readings by plot id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Environment readings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No environment readings found for the given plot")
    })
    public ResponseEntity<List<EnvironmentReadingResource>> getEnvironmentReadingsByPlotId(
            @PathVariable Long plotId) {
        var readings = environmentReadingQueryService.handle(
                new GetAllEnvironmentReadingsByPlotIdQuery(new PlotId(plotId)));
        if (readings.isEmpty()) return ResponseEntity.notFound().build();
        var resources = readings.stream()
                .map(EnvironmentReadingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Endpoint usado internamente por el reporting-service para obtener
     * las lecturas filtradas necesarias para calcular las métricas de un reporte.
     *
     * GET /api/v1/environment-readings/plot/{plotId}/filter
     *      ?type=TEMPERATURE&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
     */
    @GetMapping("/plot/{plotId}/filter")
    @Operation(summary = "Get environment readings by plot, type and date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Readings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reading type")
    })
    public ResponseEntity<List<ReadingResource>> getReadingsByPlotAndTypeAndPeriod(
            @PathVariable Long plotId,
            @RequestParam String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        ReadingType readingType;
        try {
            readingType = ReadingType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        var query = new GetEnvironmentReadingsByPlotIdAndTypeAndPeriodQuery(
                new PlotId(plotId), readingType, start, end);

        var readings = environmentReadingQueryService.handle(query);

        var resources = readings.stream()
                .map(r -> new ReadingResource(
                        r.getReadingValue().getValue(),
                        r.getReadingValue().getUnit(),
                        r.getMeasuredAt()))
                .toList();

        return ResponseEntity.ok(resources);
    }
}
