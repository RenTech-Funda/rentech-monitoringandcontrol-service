package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

/***
 * Command to delete an existing task.
 * @param taskId the ID of the task to be deleted.
 */
public record DeleteTaskCommand (Long taskId){
    /**
     * Creates a command to delete an existing task.
     * @param taskId the ID of the task to be deleted (must be greater than 0)
     * @throws IllegalArgumentException if taskId is null or invalid
     */
    public DeleteTaskCommand {
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("taskId cannot be null or less than or equal to zero");
        }
    }
}
