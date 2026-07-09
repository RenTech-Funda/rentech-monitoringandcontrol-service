package com.floweytech.agrotrack.monitoringservice.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageClient {
    String uploadPlantObservationImage(Long sessionId, Long observationId, MultipartFile file);
}
