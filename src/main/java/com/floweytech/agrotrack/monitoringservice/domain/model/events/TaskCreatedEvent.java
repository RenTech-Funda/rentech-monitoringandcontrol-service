package com.floweytech.agrotrack.monitoringservice.domain.model.events;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.*;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class TaskCreatedEvent extends ApplicationEvent {
    private final Long taskId;
    private final ProfileId assigneeProfileId;
    private final ProfileId assignedToProfileId;
    private final OrganizationId organizationId;
    private final TaskDetails taskDetails;
    private final DateRange dateRange;
    private final TaskStatus taskStatus;
    private final List<MaterialUsed> materialsUsed;

    /**
     * Constructor for TaskCreatedEvent.
     * @param taskId The ID of the created task
     * @param source The source aggregate that published this event
     * @param assigneeProfileId The profile who assigns the task
     * @param assignedToProfileId The profile to which the task was assigned
     * @param organizationId The organization ID
     * @param taskDetails The title and description of the task
     * @param dateRange The execution date range
     * @param taskStatus The current task status
     * @param materialsUsed The list of materials used for the task
     */
    public TaskCreatedEvent(
            Object source,
            Long taskId,
            ProfileId assigneeProfileId,
            ProfileId assignedToProfileId,
            OrganizationId organizationId,
            TaskDetails taskDetails,
            DateRange dateRange,
            TaskStatus taskStatus,
            List<MaterialUsed> materialsUsed
    ){
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
