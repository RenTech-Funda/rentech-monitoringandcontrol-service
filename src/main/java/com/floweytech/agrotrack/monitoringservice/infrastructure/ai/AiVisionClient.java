package com.floweytech.agrotrack.monitoringservice.infrastructure.ai;

public interface AiVisionClient {
    PlantPhotoAnalysisResult analyzePlantPhoto(String imageUrl);
}
