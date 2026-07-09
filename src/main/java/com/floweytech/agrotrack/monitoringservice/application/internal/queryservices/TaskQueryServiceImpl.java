package com.floweytech.agrotrack.monitoringservice.application.internal.queryservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.Task;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.*;
import com.floweytech.agrotrack.monitoringservice.domain.services.TaskQueryService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskQueryServiceImpl implements TaskQueryService {
    private final TaskRepository taskRepository;

    public TaskQueryServiceImpl(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> handle(GetAllTasksQuery query) {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> handle(GetTaskByIdQuery query) {
        return taskRepository.findById(query.TaskId());
    }

    @Override
    public List<Task> handle(GetTasksByCreatedByUserIdQuery query) {
        return taskRepository.findByCreatedByUserId_UserId(query.createdByUserId());
    }

    @Override
    public List<Task> handle(GetTasksByAssignedToUserIdQuery query) {
        return taskRepository.findByAssignedToUserId_UserId(query.assignedToUserId());
    }

    @Override
    public List<Task> handle(GetTasksByOrganizationIdQuery query) {
        return taskRepository.findByOrganizationId_OrganizationId(query.organizationId());
    }
}
