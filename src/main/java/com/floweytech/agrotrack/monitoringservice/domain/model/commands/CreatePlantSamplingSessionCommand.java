package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;

import java.time.LocalDateTime;

public record CreatePlantSamplingSessionCommand(
        PlotId plotId,
        LocalDateTime sampledAt) {

    public CreatePlantSamplingSessionCommand {
        if (plotId == null)
            throw new IllegalArgumentException("plotId cannot be null");
        if (sampledAt == null)
            throw new IllegalArgumentException("sampledAt cannot be null");
    }

}
