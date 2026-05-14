package com.floweytech.agrotrack.monitoringservice.domain.services;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.Task;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreateTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.DeleteTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.ModifyTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdateTaskStatusCommand;

import java.util.Optional;

public interface TaskCommandService {
    /**
     * Handles the creation of a new task.
     */
    Optional<Task> handle(CreateTaskCommand command);
    /**
     * Handles the deletion of an existing task.
     */
    Optional<Task> handle(DeleteTaskCommand command);
    /**
     * Handles the modification of an existing task.
     */
    Optional<Task> handle(ModifyTaskCommand command);
    /**
     * Handles the update of task status.
     */
    Optional<Task> handle(UpdateTaskStatusCommand command);
}
