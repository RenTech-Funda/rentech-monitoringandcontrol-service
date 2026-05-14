package com.floweytech.agrotrack.monitoringservice.domain.model.events;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlantObservationAddedEvent extends ApplicationEvent {

    private final Long sessionId;
    private final Long observationId;

    public PlantObservationAddedEvent(PlantSamplingSession source, Long sessionId, Long observationId) {
        super(source);
        this.sessionId = sessionId;
        this.observationId = observationId;
    }

    public Long getSessionId() { return sessionId; }
    public Long getObservationId() { return observationId; }
}
