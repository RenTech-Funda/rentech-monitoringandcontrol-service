package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record MaterialUsedResource(
        String materialName,
        Double quantity,
        String unit
) {}
