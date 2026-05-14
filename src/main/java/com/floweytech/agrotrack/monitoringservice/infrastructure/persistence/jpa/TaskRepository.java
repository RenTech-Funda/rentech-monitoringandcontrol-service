package com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.Task;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    /**
     * Find tasks by assignee profile ID (who assigns the task)
     * @param profileId The assignee profile ID
     * @return List of tasks
     */
    List<Task> findByAssigneeProfileId_ProfileId(Long profileId);

    /**
     * Find tasks by assigned to profile ID (who receives the task)
     * @param profileId The assigned to profile ID
     * @return List of tasks
     */
    List<Task> findByAssignedToProfileId_ProfileId(Long profileId);

    /**
     * Find tasks by organization ID
     * @param organizationId The organization ID
     * @return List of tasks
     */
    List<Task> findByOrganizationId_OrganizationId(Long organizationId);

    /**
     * Find tasks by status
     * @param status The task status
     * @return List of tasks
     */
    List<Task> findByTaskStatus(TaskStatus status);

}
