package com.floweytech.agrotrack.monitoringservice.application.internal.commandservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.Task;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreateTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.DeleteTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.ModifyTaskCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdateTaskStatusCommand;
import com.floweytech.agrotrack.monitoringservice.domain.services.TaskCommandService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Task Command Service Implementation
 */
@Service
public class TaskCommandServicelmlp implements TaskCommandService {
    private final TaskRepository taskRepository;

    /**
     * Constructor
     * @param taskRepository The {@link TaskRepository} instance
     */
    public TaskCommandServicelmlp(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @Transactional
    @Override
    public Optional<Task> handle(CreateTaskCommand command) {
        var task = new Task(command);
        taskRepository.save(task);
        return Optional.of(task);
    }

    @Transactional
    @Override
    public Optional<Task> handle(DeleteTaskCommand command){
        return taskRepository.findById(command.taskId()).map(task -> {
            task.deleteTask();
            taskRepository.delete(task);
            return task;

        });
    }

    @Transactional
    @Override
    public Optional<Task> handle(ModifyTaskCommand command) {
        return taskRepository.findById(command.taskId()).map(existingTask -> {
            existingTask.applyTaskModification(command);
            taskRepository.save(existingTask);
            return existingTask;
        });
    }

    @Transactional
    @Override
    public Optional<Task> handle(UpdateTaskStatusCommand command) {
        return taskRepository.findById(command.taskId()).map(existingTask -> {
            existingTask.updateStatus(command.taskStatus());
            taskRepository.save(existingTask);
            return existingTask;
        });
    }

}
