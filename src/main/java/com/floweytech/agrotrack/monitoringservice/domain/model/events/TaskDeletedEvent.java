package com.floweytech.agrotrack.monitoringservice.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


/**
 * TaskDeletedEvent
 * Event representing the deletion of a task.
 */
@Getter
public class TaskDeletedEvent  extends ApplicationEvent {
    private final Long taskId;

    /**
     * Constructor for TaskDeletedEvent.
     * @param source
     * @param taskId
     */
    public TaskDeletedEvent(Object source, Long taskId){
        super(source);
        this.taskId = taskId;
    }
}
