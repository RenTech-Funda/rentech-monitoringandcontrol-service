package com.floweytech.agrotrack.monitoringservice.application.internal.eventhandlers;

import com.floweytech.agrotrack.monitoringservice.domain.model.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PlantSamplingSessionEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlantSamplingSessionEventHandler.class);

    @EventListener
    public void handle(PlantSamplingSessionCreatedEvent event) {
        LOGGER.info("PlantSamplingSession created: sessionId={}", event.getSessionId());
    }

    @EventListener
    public void handle(PlantObservationAddedEvent event) {
        LOGGER.info("Plant observation added: sessionId={}, observationId={}",
                event.getSessionId(), event.getObservationId());
    }

    @EventListener
    public void handle(PlantObservationUpdatedEvent event) {
        LOGGER.info("Plant observation updated: sessionId={}, observationId={}",
                event.getSessionId(), event.getObservationId());
    }

    @EventListener
    public void handle(PlantObservationRemovedEvent event) {
        LOGGER.info("Plant observation removed: sessionId={}, observationId={}",
                event.getSessionId(), event.getObservationId());
    }
}
