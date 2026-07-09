package com.floweytech.agrotrack.monitoringservice.domain.model.queries;

public record GetTasksByCreatedByUserIdQuery(Long createdByUserId) {
    public GetTasksByCreatedByUserIdQuery {
        if (createdByUserId == null || createdByUserId <= 0)
            throw new IllegalArgumentException("createdByUserId cannot be null or less than or equal to zero");
    }
}
