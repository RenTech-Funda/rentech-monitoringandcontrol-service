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
    @AttributeOverride(name = "userId", column = @Column(name = "created_by_user_id"))
    @Getter
    private UserId createdByUserId;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "assigned_to_user_id"))
    @Getter
    private UserId assignedToUserId;

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
        this.createdByUserId = command.createdByUserId();
        this.assignedToUserId = command.assignedToUserId();
        this.organizationId = command.organizationId();
        this.taskDetails = command.taskDetails();
        this.dateRange = command.dateRange();
        this.taskStatus = command.taskStatus();
        this.materialsUsed = command.materialsUsed();

        this.registerEvent(new TaskCreatedEvent(
                this,
                this.getId(),
                this.createdByUserId,
                this.assignedToUserId,
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

        this.createdByUserId = command.createdByUserId();
        this.assignedToUserId = command.assignedToUserId();
        this.organizationId = command.organizationId();
        this.taskDetails = command.taskDetails();
        this.dateRange = command.dateRange();
        this.taskStatus = command.taskStatus();
        this.materialsUsed = command.materialsUsed();

        this.registerEvent(new TaskModifiedEvent(
                this,
                this.getId(),
                this.createdByUserId,
                this.assignedToUserId,
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

    public void setCreatedByUserId(UserId createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public void setAssignedToUserId(UserId assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
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
