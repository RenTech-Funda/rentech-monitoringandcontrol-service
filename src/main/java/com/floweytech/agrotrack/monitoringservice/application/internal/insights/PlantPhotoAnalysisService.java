package com.floweytech.agrotrack.monitoringservice.application.internal.insights;

import com.floweytech.agrotrack.monitoringservice.domain.model.entities.PlantObservation;
import com.floweytech.agrotrack.monitoringservice.infrastructure.ai.AiVisionClient;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantSamplingSessionRepository;
import com.floweytech.agrotrack.monitoringservice.infrastructure.storage.ImageStorageClient;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.PlantPhotoAnalysisResource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PlantPhotoAnalysisService {

    private final PlantSamplingSessionRepository plantSamplingSessionRepository;
    private final AiVisionClient aiVisionClient;
    private final ImageStorageClient imageStorageClient;

    public PlantPhotoAnalysisService(
            PlantSamplingSessionRepository plantSamplingSessionRepository,
            AiVisionClient aiVisionClient,
            ImageStorageClient imageStorageClient) {
        this.plantSamplingSessionRepository = plantSamplingSessionRepository;
        this.aiVisionClient = aiVisionClient;
        this.imageStorageClient = imageStorageClient;
    }

    @Transactional
    public PlantPhotoAnalysisResource analyzeFromImageUrl(Long sessionId, Long observationId, String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("imageUrl cannot be null or blank");
        }
        return analyzeAndPersist(sessionId, observationId, imageUrl);
    }

    @Transactional
    public PlantPhotoAnalysisResource analyzeFromUpload(Long sessionId, Long observationId, MultipartFile file) {
        var imageUrl = imageStorageClient.uploadPlantObservationImage(sessionId, observationId, file);
        return analyzeAndPersist(sessionId, observationId, imageUrl);
    }

    private PlantPhotoAnalysisResource analyzeAndPersist(Long sessionId, Long observationId, String imageUrl) {
        var session = plantSamplingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("PlantSamplingSession not found"));

        var observation = session.getObservations().stream()
                .filter(item -> item.getId().equals(observationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("PlantObservation not found"));

        var analysis = aiVisionClient.analyzePlantPhoto(imageUrl);
        observation.applyPhotoAnalysis(
                imageUrl,
                analysis.detectedIssue(),
                analysis.diagnosis(),
                analysis.recommendation(),
                analysis.confidence()
        );

        plantSamplingSessionRepository.save(session);
        return toResource(observation);
    }

    private PlantPhotoAnalysisResource toResource(PlantObservation observation) {
        return new PlantPhotoAnalysisResource(
                observation.getId(),
                observation.getImageUrl(),
                observation.getDetectedIssue(),
                observation.getAiDiagnosis(),
                observation.getAiRecommendation(),
                observation.getConfidence()
        );
    }
}
