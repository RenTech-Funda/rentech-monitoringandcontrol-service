package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlantObservationData;

public record UpdatePlantObservationCommand(
        Long observationId,
        PlantObservationData observationData
) {
    public UpdatePlantObservationCommand {
        if (observationId == null)
            throw new IllegalArgumentException("observationId cannot be null");
        if (observationData == null)
            throw new IllegalArgumentException("observationData cannot be null");
    }
}
