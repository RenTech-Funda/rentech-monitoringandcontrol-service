package com.floweytech.agrotrack.monitoringservice.infrastructure.ai;

public record PlantPhotoAnalysisResult(
        String detectedIssue,
        String diagnosis,
        String recommendation,
        Double confidence
) {
    public static PlantPhotoAnalysisResult fallback() {
        return new PlantPhotoAnalysisResult(
                "VISUAL_REVIEW_REQUIRED",
                "La imagen fue registrada, pero no se pudo completar el análisis automático.",
                "Realizar una inspección visual y repetir el análisis cuando el servicio de IA esté disponible.",
                0.0
        );
    }
}
