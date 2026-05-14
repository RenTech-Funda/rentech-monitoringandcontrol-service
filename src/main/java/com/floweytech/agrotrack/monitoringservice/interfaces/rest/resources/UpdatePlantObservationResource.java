package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record UpdatePlantObservationResource(
        Double heightCm,
        Integer leafCount,
        Integer fruitCount,
        String notes
) {}
