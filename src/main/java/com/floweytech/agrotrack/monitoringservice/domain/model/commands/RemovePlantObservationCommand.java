package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

public record RemovePlantObservationCommand(
        Long observationId
) {
    public RemovePlantObservationCommand {
        if (observationId == null)
            throw new IllegalArgumentException("observationId cannot be null");
    }
}
