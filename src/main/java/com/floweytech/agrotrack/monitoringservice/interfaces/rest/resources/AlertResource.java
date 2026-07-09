package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record AlertResource(
        String type,
        String severity,
        String title,
        String message
) {
}
