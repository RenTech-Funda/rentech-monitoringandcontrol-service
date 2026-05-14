package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.List;

public record PlantSamplingSessionResource(
        Long id,
        Long plotId,
        LocalDateTime sampledAt,
        SampleAverageResource average,
        List<PlantObservationResource> observations
) {}
