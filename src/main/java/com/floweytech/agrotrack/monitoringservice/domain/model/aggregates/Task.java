package com.floweytech.agrotrack.monitoringservice.domain.model.aggregates;


import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreateTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.ModifyTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.TaskCreatedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.TaskDeletedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.TaskModifiedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.*;
import com.floweytech.agrotrack.monitoringservice.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Task extends AuditableAbstractAggregateRoot<Task> {

    @Embedded
    @AttributeOverride(name = "profileId", column = @Column(name = "assignee_profile_id"))
    @Getter
    private ProfileId assigneeProfileId;

    @Embedded
    @AttributeOverride(name = "profileId", column = @Column(name = "assigned_to_profile_id"))
    @Getter
    private ProfileId assignedToProfileId;

    @Embedded
    @AttributeOverride(name = "organizationId", column = @Column(name = "organization_id"))
    @Getter
    private OrganizationId organizationId;

    @Embedded
    @Getter
    private TaskDetails taskDetails;

    @Embedded
    @Getter
    private DateRange dateRange;

    @Enumerated(EnumType.STRING)
    @Getter
    private TaskStatus taskStatus;

    @ElementCollection
    @CollectionTable(name = "task_materials_used", joinColumns = @JoinColumn(name = "task_id"))
    @Getter
    private List<MaterialUsed> materialsUsed;

    @Column(nullable = false)
    @LastModifiedDate
    @Getter
    private LocalDateTime updatedAt;

    protected Task() {}

    public Task(CreateTaskCommand command) {
        this.assigneeProfileId = command.assigneeProfileId();
        this.assignedToProfileId = command.assignedToProfileId();
        this.organizationId = command.organizationId();
        this.taskDetails = command.taskDetails();
        this.dateRange = command.dateRange();
        this.taskStatus = command.taskStatus();
        this.materialsUsed = command.materialsUsed();

        this.registerEvent(new TaskCreatedEvent(
                this,
                this.getId(),
                this.assigneeProfileId,
                this.assignedToProfileId,
                this.organizationId,
                this.taskDetails,
                this.dateRange,
                this.taskStatus,
                this.materialsUsed

        ));
    }

    /**
     * Applies modifications to the task based on the provided command.
     * Publishes a TaskModifiedEvent after successful modification.
     * @param command The command containing the new task details
     */
    public void applyTaskModification(ModifyTaskCommand command){

        this.assigneeProfileId = command.assigneeProfileId();
        this.assignedToProfileId = command.assignedToProfileId();
        this.organizationId = command.organizationId();
        this.taskDetails = command.taskDetails();
        this.dateRange = command.dateRange();
        this.taskStatus = command.taskStatus();
        this.materialsUsed = command.materialsUsed();

        this.registerEvent(new TaskModifiedEvent(
                this,
                this.getId(),
                this.assigneeProfileId,
                this.assignedToProfileId,
                this.organizationId,
                this.taskDetails,
                this.dateRange,
                this.taskStatus,
                this.materialsUsed
        ));
    }


    public void deleteTask() {
        this.registerEvent(new TaskDeletedEvent(
                this,
                this.getId()
        ));
    }

    /**
     * Sets the assignee profile ID (who assigns the task).
     * @param assigneeProfileId The profile ID of the assignee
     */
    public void setAssigneeProfileId(ProfileId assigneeProfileId) {
        this.assigneeProfileId = assigneeProfileId;
    }

    /**
     * Sets the assigned to profile ID (who receives the task).
     * @param assignedToProfileId The profile ID of the assigned person
     */
    public void setAssignedToProfileId(ProfileId assignedToProfileId) {
        this.assignedToProfileId = assignedToProfileId;
    }

    /**
     * Sets the organization ID.
     * @param organizationId The organization ID
     */
    public void setOrganizationId(OrganizationId organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Updates the status of the task.
     * @param taskStatus The new task status
     */
    public void updateStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    /**
     * Adds a material used to the task.
     * @param materialUsed The material to add
     */
    public void addMaterialUsed(MaterialUsed materialUsed) {
        this.materialsUsed.add(materialUsed);
    }


}
