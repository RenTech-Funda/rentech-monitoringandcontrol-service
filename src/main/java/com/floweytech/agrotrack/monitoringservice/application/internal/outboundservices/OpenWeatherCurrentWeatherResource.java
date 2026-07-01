package com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices;

public record OpenWeatherCurrentWeatherResource(
        Main main,
        Long dt,
        String name
) {
    public record Main(
            Double temp,
            Integer humidity
    ) {
    }
}
