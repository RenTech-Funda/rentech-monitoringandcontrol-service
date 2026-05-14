package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

import java.time.LocalDateTime;

public record CreatePlantSamplingSessionResource(
        Long plotId,
        LocalDateTime sampledAt
) {}
