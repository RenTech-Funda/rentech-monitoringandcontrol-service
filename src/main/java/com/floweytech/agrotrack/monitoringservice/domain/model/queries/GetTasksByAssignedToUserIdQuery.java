package com.floweytech.agrotrack.monitoringservice.domain.model.queries;

public record GetTasksByAssignedToUserIdQuery(Long assignedToUserId) {
    public GetTasksByAssignedToUserIdQuery {
        if (assignedToUserId == null || assignedToUserId <= 0)
            throw new IllegalArgumentException("assignedToUserId cannot be null or less than or equal to zero");
    }
}
