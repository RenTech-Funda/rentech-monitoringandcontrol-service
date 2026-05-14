package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.*;

import java.util.List;

/**
 * Command to modify an existing task with updated details, date range, status, and materials.
 * Validates that all required value objects are provided.
 */
public record ModifyTaskCommand (
    Long taskId,
    ProfileId assigneeProfileId,
    ProfileId assignedToProfileId,
    OrganizationId organizationId,
    TaskDetails taskDetails,
    DateRange dateRange,
    TaskStatus taskStatus,
    List<MaterialUsed> materialsUsed
){
    public  ModifyTaskCommand{
        if (taskId == null)
            throw new IllegalArgumentException("taskId cannot be null");
        if (assigneeProfileId == null)
            throw new IllegalArgumentException("assigneeProfileId cannot be null");
        if (assignedToProfileId == null)
            throw new IllegalArgumentException("assignedToProfileId cannot be null");
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
