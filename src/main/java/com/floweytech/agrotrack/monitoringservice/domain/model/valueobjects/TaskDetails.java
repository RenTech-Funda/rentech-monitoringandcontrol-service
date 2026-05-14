package com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class TaskDetails  {
    private String taskTitle;
    private String taskDescription;

    protected TaskDetails() {
    }
    public TaskDetails(String taskTitle, String taskDescription) {
        if (taskTitle == null || taskTitle.isBlank())
            throw new IllegalArgumentException("taskTitle cannot be null or blank");
        if (taskTitle.length() > 100)
            throw new IllegalArgumentException("taskTitle cannot be longer than 100 characters");
        if (taskDescription == null || taskDescription.isBlank())
            throw new IllegalArgumentException("taskDescription cannot be null or blank");
        if (taskDescription.length() > 500)
            throw new IllegalArgumentException("taskDescription cannot be longer than 500 characters");
        this.taskDescription = taskDescription;
        this.taskTitle = taskTitle;

    }
}
