package com.floweytech.agrotrack.monitoringservice.interfaces.rest.controllers;

import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsByPlotIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetEnvironmentReadingByIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.services.EnvironmentReadingCommandService;
import com.floweytech.agrotrack.monitoringservice.domain.services.EnvironmentReadingQueryService;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.CreateEnvironmentReadingResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.EnvironmentReadingResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform.CreateEnvironmentReadingCommandFromResourceAssembler;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform.EnvironmentReadingResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/environment-readings", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Environment Readings", description = "Environment Readings Management Endpoints")
public class EnvironmentReadingsController {

    private final EnvironmentReadingCommandService environmentReadingCommandService;
    private final EnvironmentReadingQueryService environmentReadingQueryService;

    /**
     * Instantiates a new {@link EnvironmentReadingsController} instance.
     */
    public EnvironmentReadingsController(EnvironmentReadingCommandService environmentReadingCommandService,
                                         EnvironmentReadingQueryService environmentReadingQueryService) {
        this.environmentReadingCommandService = environmentReadingCommandService;
        this.environmentReadingQueryService = environmentReadingQueryService;
    }

    /**
     * Create a new environmental reading.
     * @param resource The {@link CreateEnvironmentReadingResource} instance.
     * @return A {@link EnvironmentReadingResource} for the created Environment Reading
     *         or a bad request response if the creation failed.
     */
    @PostMapping
    @Operation(summary = "Create a new environment reading")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Environment reading created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input data")
    })
    public ResponseEntity<EnvironmentReadingResource> createEnvironmentReading(
            @RequestBody CreateEnvironmentReadingResource resource) {

        var createEnvironmentReadingCommand =
                CreateEnvironmentReadingCommandFromResourceAssembler.toCommandFromResource(resource);

        var environmentReading = environmentReadingCommandService.handle(createEnvironmentReadingCommand);

        if (environmentReading.isEmpty())
            return ResponseEntity.badRequest().build();

        var createdEnvironmentReading = environmentReading.get();
        var environmentReadingResource =
                EnvironmentReadingResourceFromEntityAssembler.toResourceFromEntity(createdEnvironmentReading);

        return new ResponseEntity<>(environmentReadingResource, HttpStatus.CREATED);
    }

    /**
     * Get list of all environmental readings.
     * @return A list of {@link EnvironmentReadingResource} resources for all EnvironmentReading, or a not found response if no EnvironmentReadings are found.
     */
    @GetMapping
    @Operation(summary = "Get all environment readings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of environment readings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No environment readings found")
    })
    public  ResponseEntity<List<EnvironmentReadingResource>> getAllEnvironmentReadings() {
        var environmentReadings = environmentReadingQueryService.handle(new GetAllEnvironmentReadingsQuery());
        if (environmentReadings.isEmpty()) return ResponseEntity.notFound().build();
        var environmentReadingResources = environmentReadings.stream()
                .map(EnvironmentReadingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(environmentReadingResources);
    }

    /**
     * Get an Environment Reading by its id.
     * @param environmentReadingId The id of the Environment Reading.
     * @return A {@link EnvironmentReadingResource} for the Environment Reading with the given id,
     *         or a not found response if no Environment Reading with the given id exists.
     */
    @GetMapping("/{environmentReadingId}")
    @Operation(summary = "Get an environment reading by id")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Environment reading retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Environment reading not found")
    })
    public ResponseEntity<EnvironmentReadingResource> getEnvironmentReadingById(@PathVariable Long environmentReadingId) {
        var getEnvironmentReadingByIdQuery = new GetEnvironmentReadingByIdQuery(environmentReadingId);
        var environmentReading = environmentReadingQueryService.handle(getEnvironmentReadingByIdQuery);
        if (environmentReading.isEmpty()) return ResponseEntity.notFound().build();
        var environmentReadingEntity = environmentReading.get();
        var environmentReadingResource = EnvironmentReadingResourceFromEntityAssembler.toResourceFromEntity(environmentReadingEntity);
        return ResponseEntity.ok(environmentReadingResource);
    }

    /**
     * Get all Environment Readings by plot id.
     * @param plotId The id of the plot.
     * @return A list of {@link EnvironmentReadingResource} for the Environment Readings associated with the given plot id,
     *         or a not found response if no Environment Readings are found for the given plot.
     */
    @GetMapping("/plot/{plotId}")
    @Operation(summary = "Get environment readings by plot id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Environment readings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No environment readings found for the given plot")
    })
    public ResponseEntity<List<EnvironmentReadingResource>> getEnvironmentReadingsByPlotId(@PathVariable Long plotId) {
        var getEnvironmentReadingsByPlotIdQuery = new GetAllEnvironmentReadingsByPlotIdQuery(new PlotId(plotId));
        var environmentReadings = environmentReadingQueryService.handle(getEnvironmentReadingsByPlotIdQuery);
        if (environmentReadings.isEmpty()) return ResponseEntity.notFound().build();
        var environmentReadingResources = environmentReadings.stream()
                .map(EnvironmentReadingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(environmentReadingResources);
    }

}
