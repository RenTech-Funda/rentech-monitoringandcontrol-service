package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record SuggestedTaskResource(
        String title,
        String description,
        String priority
) {
}
