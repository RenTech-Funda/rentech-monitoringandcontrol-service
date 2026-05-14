package com.floweytech.agrotrack.monitoringservice.interfaces.rest.controllers;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.DeleteTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetTaskByIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetTasksByAssignedToProfileIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetTasksByAssigneeProfileIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetTasksByOrganizationIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.services.TaskCommandService;
import com.floweytech.agrotrack.monitoringservice.domain.services.TaskQueryService;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.CreateTaskResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.TaskResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.UpdateTaskStatusResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform.CreateTaskCommandFromResourceAssembler;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform.TaskResourceFromEntityAssembler;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform.UpdateTaskStatusCommandFromResourceAssembler;
import com.floweytech.agrotrack.monitoringservice.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/tasks", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Tasks", description = "Tasks Management Endpoints")
public class TasksController {

    private final TaskCommandService taskCommandService;
    private final TaskQueryService taskQueryService;
    private final MessageSource messageSource;

    public TasksController(TaskCommandService taskCommandService,
                           TaskQueryService taskQueryService,
                           MessageSource messageSource) {
        this.taskCommandService = taskCommandService;
        this.taskQueryService = taskQueryService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input data")
    })
    public ResponseEntity<TaskResource> createTask(@RequestBody CreateTaskResource  resource){
        var createTaskCommand = CreateTaskCommandFromResourceAssembler.toCommandFromResource(resource);
        var task = taskCommandService.handle(createTaskCommand);
        if (task.isEmpty())
            return ResponseEntity.badRequest().build();

        var createdTask = task.get();
        var taskResource = TaskResourceFromEntityAssembler.toResourceFromEntity(createdTask, messageSource);
        return new ResponseEntity<>(taskResource, HttpStatus.CREATED);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input data"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResource> updateTask(
            @PathVariable Long taskId,
            @RequestBody CreateTaskResource resource) {

        var modifyTaskCommand = CreateTaskCommandFromResourceAssembler
                .toModifyCommandFromResource(taskId, resource);

        var task = taskCommandService.handle(modifyTaskCommand);

        if (task.isEmpty())
            return ResponseEntity.notFound().build();

        var updatedTask = task.get();
        var taskResource = TaskResourceFromEntityAssembler.toResourceFromEntity(updatedTask, messageSource);

        return ResponseEntity.ok(taskResource);
    }

    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Update task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid status"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResource> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskStatusResource resource) {

        try {
            var command = UpdateTaskStatusCommandFromResourceAssembler.toCommandFromResource(taskId, resource);
            var task = taskCommandService.handle(command);

            if (task.isEmpty())
                return ResponseEntity.notFound().build();

            var updatedTask = task.get();
            var taskResource = TaskResourceFromEntityAssembler.toResourceFromEntity(updatedTask, messageSource);

            return ResponseEntity.ok(taskResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResource> getTaskById(@PathVariable Long taskId){
        var getTaskByIdQuery = new GetTaskByIdQuery(taskId);
        var task = taskQueryService.handle(getTaskByIdQuery);
        if (task.isEmpty()) return ResponseEntity.notFound().build();
        var taskEntity = task.get();
        var taskResource = TaskResourceFromEntityAssembler.toResourceFromEntity(taskEntity, messageSource);
        return ResponseEntity.ok(taskResource);
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get all tasks by organization ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No tasks found")
    })
    public ResponseEntity<List<TaskResource>> getTasksByOrganizationId(@PathVariable Long organizationId){
        var query = new GetTasksByOrganizationIdQuery(organizationId);
        var tasks = taskQueryService.handle(query);
        if (tasks.isEmpty()) return ResponseEntity.notFound().build();
        var taskResources = tasks.stream()
                .map(task -> TaskResourceFromEntityAssembler.toResourceFromEntity(task, messageSource))
                .toList();
        return ResponseEntity.ok(taskResources);
    }

    @GetMapping("/assignee/{assigneeProfileId}")
    @Operation(summary = "Get all tasks by assignee profile ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No tasks found")
    })
    public ResponseEntity<List<TaskResource>> getTasksByAssigneeProfileId(@PathVariable Long assigneeProfileId){
        var query = new GetTasksByAssigneeProfileIdQuery(assigneeProfileId);
        var tasks = taskQueryService.handle(query);
        if (tasks.isEmpty()) return ResponseEntity.notFound().build();
        var taskResources = tasks.stream()
                .map(task -> TaskResourceFromEntityAssembler.toResourceFromEntity(task, messageSource))
                .toList();
        return ResponseEntity.ok(taskResources);
    }

    @GetMapping("/assigned/{assignedToProfileId}")
    @Operation(summary = "Get all tasks by assigned to profile ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No tasks found")
    })
    public ResponseEntity<List<TaskResource>> getTasksByAssignedToProfileId(@PathVariable Long assignedToProfileId){
        var query = new GetTasksByAssignedToProfileIdQuery(assignedToProfileId);
        var tasks = taskQueryService.handle(query);
        if (tasks.isEmpty()) return ResponseEntity.notFound().build();
        var taskResources = tasks.stream()
                .map(task -> TaskResourceFromEntityAssembler.toResourceFromEntity(task, messageSource))
                .toList();
        return ResponseEntity.ok(taskResources);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<MessageResource> deleteTask(@PathVariable Long taskId){
        var deleteTaskCommand = new DeleteTaskCommand(taskId);
        taskCommandService.handle(deleteTaskCommand);

        String message = messageSource.getMessage("task.deleted.success",
                null,
                LocaleContextHolder.getLocale());

        return ResponseEntity.ok(new MessageResource(message));
    }
}
