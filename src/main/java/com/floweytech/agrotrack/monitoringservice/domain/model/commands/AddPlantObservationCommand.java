package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlantObservationData;

public record AddPlantObservationCommand(
        PlantObservationData observationData
) {
    public AddPlantObservationCommand {
        if (observationData == null)
            throw new IllegalArgumentException("observationData cannot be null");
    }
}
