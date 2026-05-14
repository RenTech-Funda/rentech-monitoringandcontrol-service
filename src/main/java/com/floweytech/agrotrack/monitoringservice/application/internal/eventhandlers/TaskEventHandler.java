package com.floweytech.agrotrack.monitoringservice.application.internal.eventhandlers;

import com.floweytech.agrotrack.monitoringservice.domain.model.events.TaskCreatedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.TaskDeletedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.TaskModifiedEvent;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.TaskRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * TaskEventHandler
 * Handles domain events published by Task aggregate.
 */
@Service
public class TaskEventHandler {

    private final TaskRepository taskRepository;

    public TaskEventHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @EventListener
    public void on(TaskCreatedEvent event){

    }


    @EventListener
    public void on(TaskDeletedEvent event){

    }

    @EventListener
    public void on(TaskModifiedEvent event){

    }

}
