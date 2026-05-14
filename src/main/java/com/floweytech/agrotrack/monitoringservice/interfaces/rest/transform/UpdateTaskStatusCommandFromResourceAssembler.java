package com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdateTaskStatusCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.TaskStatus;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.UpdateTaskStatusResource;

public class UpdateTaskStatusCommandFromResourceAssembler {
    public static UpdateTaskStatusCommand toCommandFromResource(Long taskId, UpdateTaskStatusResource resource) {
        TaskStatus taskStatus;
        try {
            taskStatus = TaskStatus.valueOf(resource.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task status: " + resource.status() +
                    ". Valid values are: PENDING, IN_PROGRESS, COMPLETED, CANCELLED");
        }

        return new UpdateTaskStatusCommand(taskId, taskStatus);
    }
}

