package com.floweytech.agrotrack.monitoringservice.domain.model.events;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.*;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * TaskModifiedEvent
 * Event representing the modification of a task.
 */
@Getter
public class TaskModifiedEvent extends ApplicationEvent {
    private final Long taskId;
    private final ProfileId assigneeProfileId;
    private final ProfileId assignedToProfileId;
    private final OrganizationId organizationId;
    private final TaskDetails taskDetails;
    private final DateRange dateRange;
    private final TaskStatus taskStatus;
    private final List<MaterialUsed> materialsUsed;


    /**
     * Constructor for TaskModifiedEvent.
     * @param source The source aggregate
     * @param taskId The task ID
     * @param assigneeProfileId The profile who assigns the task
     * @param assignedToProfileId The profile to which the task is assigned
     * @param organizationId The organization ID
     * @param taskDetails The task details
     * @param dateRange The date range
     * @param taskStatus The task status
     * @param materialsUsed The materials used
     */
    public TaskModifiedEvent(
            Object source,
            Long taskId,
            ProfileId assigneeProfileId,
            ProfileId assignedToProfileId,
            OrganizationId organizationId,
            TaskDetails taskDetails,
            DateRange dateRange,
            TaskStatus taskStatus,
            List<MaterialUsed> materialsUsed
    ) {
        super(source);
        this.taskId = taskId;
        this.assigneeProfileId = assigneeProfileId;
        this.assignedToProfileId = assignedToProfileId;
        this.organizationId = organizationId;
        this.taskDetails = taskDetails;
        this.dateRange = dateRange;
        this.taskStatus = taskStatus;
        this.materialsUsed = materialsUsed;
    }
}
