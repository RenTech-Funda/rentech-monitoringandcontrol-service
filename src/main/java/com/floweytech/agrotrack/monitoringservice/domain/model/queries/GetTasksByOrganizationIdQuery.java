package com.floweytech.agrotrack.monitoringservice.domain.model.queries;

public record GetTasksByOrganizationIdQuery(Long organizationId) {
    public GetTasksByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0)
            throw new IllegalArgumentException("organizationId cannot be null or less than or equal to zero");
    }
}

