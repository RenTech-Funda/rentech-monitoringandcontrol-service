package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record PlantObservationResource(
        Long id,
        Double heightCm,
        Integer leafCount,
        Integer fruitCount,
        String notes,
        String imageUrl,
        String detectedIssue,
        String aiDiagnosis,
        String aiRecommendation,
        Double confidence
) {}
