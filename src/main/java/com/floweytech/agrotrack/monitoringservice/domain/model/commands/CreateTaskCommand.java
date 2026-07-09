package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.*;

import java.util.List;

public record CreateTaskCommand (
    UserId createdByUserId,
    UserId assignedToUserId,
    OrganizationId organizationId,
    TaskDetails taskDetails,
    DateRange dateRange,
    TaskStatus taskStatus,
    List<MaterialUsed> materialsUsed
) {
    /**
     * Validates the command.
     * @throws IllegalArgumentException if any of the required fields are null or invalid.
     */
    public CreateTaskCommand {
        if (createdByUserId == null)
            throw new IllegalArgumentException("createdByUserId cannot be null");
        if (assignedToUserId == null)
            throw new IllegalArgumentException("assignedToUserId cannot be null");
        if (organizationId == null)
            throw new IllegalArgumentException("organizationId cannot be null");
        if (taskDetails == null)
            throw new IllegalArgumentException("taskDetails cannot be null");
        if (dateRange == null)
            throw new IllegalArgumentException("dateRange cannot be null");
        if (taskStatus == null)
            throw new IllegalArgumentException("taskStatus cannot be null");
        if (materialsUsed == null)
            throw new IllegalArgumentException("materialsUsed cannot be null");
    }
}
