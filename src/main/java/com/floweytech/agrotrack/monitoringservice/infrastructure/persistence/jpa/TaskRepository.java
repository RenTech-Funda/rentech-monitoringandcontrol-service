package com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.Task;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCreatedByUserId_UserId(Long userId);

    List<Task> findByAssignedToUserId_UserId(Long userId);

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
