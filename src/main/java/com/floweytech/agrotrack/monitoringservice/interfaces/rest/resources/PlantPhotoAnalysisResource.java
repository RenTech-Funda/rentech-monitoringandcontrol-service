package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record PlantPhotoAnalysisResource(
        Long observationId,
        String imageUrl,
        String detectedIssue,
        String diagnosis,
        String recommendation,
        Double confidence
) {
}
