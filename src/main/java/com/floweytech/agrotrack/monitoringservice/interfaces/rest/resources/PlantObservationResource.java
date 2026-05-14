package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record PlantObservationResource(
        Long id,
        Double heightCm,
        Integer leafCount,
        Integer fruitCount,
        String notes
) {}
