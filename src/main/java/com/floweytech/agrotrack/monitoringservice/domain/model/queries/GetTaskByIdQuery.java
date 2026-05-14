package com.floweytech.agrotrack.monitoringservice.domain.model.queries;

/**
 * Query to get a task by its ID.
 * @param TaskId
 */
public record GetTaskByIdQuery(Long TaskId) {
    /**
     * Creates a query to get a task by its ID.
     * @param TaskId
     */
    public GetTaskByIdQuery {
        if (TaskId == null || TaskId <= 0) {
            throw new IllegalArgumentException("TaskId cannot be null or less than or equal to zero");
        }
    }
}
