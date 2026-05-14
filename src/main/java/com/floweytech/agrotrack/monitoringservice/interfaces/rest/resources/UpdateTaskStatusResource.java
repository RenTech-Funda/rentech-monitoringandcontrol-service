package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

public record UpdateTaskStatusResource(String status) {
    public UpdateTaskStatusResource {
        if (status == null || status.isBlank())
            throw new IllegalArgumentException("status cannot be null or blank");
    }
}

