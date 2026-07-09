package com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform;

import com.floweytech.agrotrack.monitoringservice.domain.model.entities.PlantObservation;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.PlantObservationResource;

public class PlantObservationResourceFromEntityAssembler {

    /**
     * Converts PlantObservation entity to PlantObservationResource.
     */
    public static PlantObservationResource toResourceFromEntity(PlantObservation entity) {
        var data = entity.getPlantObservationData();

        return new PlantObservationResource(
                entity.getId(),
                data.heightCm(),
                data.leafCount(),
                data.fruitCount(),
                data.notes(),
                entity.getImageUrl(),
                entity.getDetectedIssue(),
                entity.getAiDiagnosis(),
                entity.getAiRecommendation(),
                entity.getConfidence()
        );
    }
}
