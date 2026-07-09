package com.floweytech.agrotrack.monitoringservice.interfaces.rest.controllers;

import com.floweytech.agrotrack.monitoringservice.application.internal.insights.PlantPhotoAnalysisService;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.AnalyzePlantPhotoResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.PlantPhotoAnalysisResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/plant-sampling-sessions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Plant Photo Analysis", description = "AI-assisted plant photo analysis endpoints")
public class PlantPhotoAnalysisController {

    private final PlantPhotoAnalysisService plantPhotoAnalysisService;

    public PlantPhotoAnalysisController(PlantPhotoAnalysisService plantPhotoAnalysisService) {
        this.plantPhotoAnalysisService = plantPhotoAnalysisService;
    }

    @PostMapping(
            value = "/{sessionId}/observations/{observationId}/photo-analysis",
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PlantPhotoAnalysisResource> analyzePhotoFromUrl(
            @PathVariable Long sessionId,
            @PathVariable Long observationId,
            @Valid @RequestBody AnalyzePlantPhotoResource resource) {
        return ResponseEntity.ok(plantPhotoAnalysisService.analyzeFromImageUrl(
                sessionId,
                observationId,
                resource.imageUrl()
        ));
    }

    @PostMapping(
            value = "/{sessionId}/observations/{observationId}/photo-analysis/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlantPhotoAnalysisResource> analyzePhotoFromUpload(
            @PathVariable Long sessionId,
            @PathVariable Long observationId,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(plantPhotoAnalysisService.analyzeFromUpload(sessionId, observationId, file));
    }
}
