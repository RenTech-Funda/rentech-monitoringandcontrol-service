package com.floweytech.agrotrack.monitoringservice.interfaces.rest.controllers;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.RemovePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllPlantSamplingSessionsQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetObservationsBySessionIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetPlantSamplingSessionByIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetPlantSamplingSessionsByPlotIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.services.PlantSamplingSessionCommandService;
import com.floweytech.agrotrack.monitoringservice.domain.services.PlantSamplingSessionQueryService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantSamplingSessionRepository;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.*;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/plant-sampling-sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Plant Sampling Sessions", description = "Plant Sampling Session Management Endpoints")
public class PlantSamplingSessionsController {
    private final PlantSamplingSessionCommandService commandService;
    private final PlantSamplingSessionQueryService queryService;

    public PlantSamplingSessionsController(PlantSamplingSessionCommandService commandService, PlantSamplingSessionQueryService queryService, PlantSamplingSessionRepository repository) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a new plant sampling session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sampling session created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input data")
    })
    public ResponseEntity<PlantSamplingSessionResource> createSamplingSession(
            @RequestBody CreatePlantSamplingSessionResource resource) {

        var command = CreatePlantSamplingSessionCommandFromResourceAssembler.toCommandFromResource(resource);

        Long sessionId = commandService.handle(command);

        // Query the created session
        var query = new GetPlantSamplingSessionByIdQuery(sessionId);
        var sessionOpt = queryService.handle(query);

        if (sessionOpt.isEmpty())
            return ResponseEntity.badRequest().build(); // should not happen

        var resourceOut = PlantSamplingSessionResourceFromEntityAssembler.toResourceFromEntity(sessionOpt.get());

        return new ResponseEntity<>(resourceOut, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all plant sampling sessions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sampling sessions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No sampling sessions found")
    })
    public ResponseEntity<List<PlantSamplingSessionResource>> getAllSamplingSessions() {

        var sessions = queryService.handle(new GetAllPlantSamplingSessionsQuery());

        if (sessions.isEmpty())
            return ResponseEntity.notFound().build();

        var resources = sessions.stream()
                .map(PlantSamplingSessionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get a sampling session by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sampling session retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Sampling session not found")
    })
    public ResponseEntity<PlantSamplingSessionResource> getSamplingSessionById(@PathVariable Long sessionId) {

        var sessionOpt = queryService.handle(new GetPlantSamplingSessionByIdQuery(sessionId));

        if (sessionOpt.isEmpty())
            return ResponseEntity.notFound().build();

        var resource = PlantSamplingSessionResourceFromEntityAssembler.toResourceFromEntity(sessionOpt.get());
        return ResponseEntity.ok(resource);
    }


    @GetMapping("/plot/{plotId}")
    @Operation(summary = "Get sampling sessions by plot id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sampling sessions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No sampling sessions found for the given plot")
    })
    public ResponseEntity<List<PlantSamplingSessionResource>> getSamplingSessionsByPlotId(@PathVariable Long plotId) {

        var query = new GetPlantSamplingSessionsByPlotIdQuery(new PlotId(plotId));
        var sessions = queryService.handle(query);

        if (sessions.isEmpty())
            return ResponseEntity.notFound().build();

        var resources = sessions.stream()
                .map(PlantSamplingSessionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @PostMapping("/{sessionId}/observations")
    @Operation(summary = "Add an observation to a sampling session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Observation added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Sampling session not found")
    })
    public ResponseEntity<PlantObservationResource> addObservation(
            @PathVariable Long sessionId,
            @RequestBody AddPlantObservationResource resource) {

        var command = AddPlantObservationCommandFromResourceAssembler.toCommandFromResource(resource);

        Long observationId = commandService.handle(sessionId, command);

        // Query session to return the new observation
        var observations = queryService.handle(new GetObservationsBySessionIdQuery(sessionId));

        var createdObs = observations.stream()
                .filter(o -> o.getId().equals(observationId))
                .findFirst()
                .orElse(null);

        if (createdObs == null)
            return ResponseEntity.badRequest().build();

        var resourceOut = PlantObservationResourceFromEntityAssembler.toResourceFromEntity(createdObs);

        return new ResponseEntity<>(resourceOut, HttpStatus.CREATED);
    }

    @PutMapping("/{sessionId}/observations/{observationId}")
    @Operation(summary = "Update an observation in a sampling session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Observation updated successfully"),
            @ApiResponse(responseCode = "404", description = "Observation or session not found")
    })
    public ResponseEntity<?> updateObservation(
            @PathVariable Long sessionId,
            @PathVariable Long observationId,
            @RequestBody UpdatePlantObservationResource resource) {

        var command =
                UpdatePlantObservationCommandFromResourceAssembler.toCommandFromResource(observationId, resource);

        commandService.handle(sessionId, command);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{sessionId}/observations/{observationId}")
    @Operation(summary = "Remove an observation from a sampling session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Observation removed successfully"),
            @ApiResponse(responseCode = "404", description = "Session or observation not found")
    })
    public ResponseEntity<?> removeObservation(
            @PathVariable Long sessionId,
            @PathVariable Long observationId) {

        var command = new RemovePlantObservationCommand(observationId);

        commandService.handle(sessionId, command);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}/observations")
    @Operation(summary = "Get observations of a sampling session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Observations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found or has no observations")
    })
    public ResponseEntity<List<PlantObservationResource>> getObservations(@PathVariable Long sessionId) {

        var observations = queryService.handle(new GetObservationsBySessionIdQuery(sessionId));

        if (observations.isEmpty())
            return ResponseEntity.notFound().build();

        var resources = observations.stream()
                .map(PlantObservationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }
}
