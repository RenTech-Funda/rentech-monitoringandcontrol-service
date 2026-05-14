package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.TaskStatus;

public record UpdateTaskStatusCommand(Long taskId, TaskStatus taskStatus) {
    public UpdateTaskStatusCommand {
        if (taskId == null || taskId <= 0)
            throw new IllegalArgumentException("taskId cannot be null or less than or equal to zero");
        if (taskStatus == null)
            throw new IllegalArgumentException("taskStatus cannot be null");
    }
}

