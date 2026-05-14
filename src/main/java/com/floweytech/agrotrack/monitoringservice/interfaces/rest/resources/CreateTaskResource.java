package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public record CreateTaskResource(
        Long assigneeProfileId,
        Long assignedToProfileId,
        Long organizationId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        TaskStatus taskStatus,
        List<MaterialUsedResource> materialsUsed
) {
    /**
     * Compact constructor for validation.
     * @throws IllegalArgumentException if any field is invalid.
     */
    public CreateTaskResource {
        if (assigneeProfileId == null || assigneeProfileId <= 0)
            throw new IllegalArgumentException("assigneeProfileId cannot be null or less than or equal to zero");
        if (assignedToProfileId == null || assignedToProfileId <= 0)
            throw new IllegalArgumentException("assignedToProfileId cannot be null or less than or equal to zero");
        if (organizationId == null || organizationId <= 0)
            throw new IllegalArgumentException("organizationId cannot be null or less than or equal to zero");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("title cannot be null or blank");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("description cannot be null or blank");
        if (startDate == null)
            throw new IllegalArgumentException("startDate cannot be null");
        if (endDate == null)
            throw new IllegalArgumentException("endDate cannot be null");
        if (taskStatus == null)
            throw new IllegalArgumentException("taskStatus cannot be null");
        if (materialsUsed == null)
            throw new IllegalArgumentException("materialsUsed cannot be null");
    }
}
