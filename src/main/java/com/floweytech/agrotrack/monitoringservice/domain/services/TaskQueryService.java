package com.floweytech.agrotrack.monitoringservice.domain.services;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.Task;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface TaskQueryService {
    List<Task> handle(GetAllTasksQuery query);
    Optional<Task> handle(GetTaskByIdQuery query);
    List<Task> handle(GetTasksByCreatedByUserIdQuery query);
    List<Task> handle(GetTasksByAssignedToUserIdQuery query);
    List<Task> handle(GetTasksByOrganizationIdQuery query);
}
