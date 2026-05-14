package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record AddPlantObservationResource(
        Double heightCm,
        Integer leafCount,
        Integer fruitCount,
        String notes
) {}
