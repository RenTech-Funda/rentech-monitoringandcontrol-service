package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record RecommendationResource(
        String code,
        String priority,
        String message
) {
}
