package com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices;

public record OpenWeatherGeoResource(
        String name,
        Double lat,
        Double lon,
        String country,
        String state
) {
}
