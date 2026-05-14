package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record SampleAverageResource(
        Double avgHeightCm,
        Double avgLeafCount,
        Double avgFruitCount
) {}
