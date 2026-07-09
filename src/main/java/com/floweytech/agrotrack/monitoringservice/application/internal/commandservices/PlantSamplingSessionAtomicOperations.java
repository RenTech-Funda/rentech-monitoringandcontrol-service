package com.floweytech.agrotrack.monitoringservice.application.internal.commandservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.AddPlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreatePlantSamplingSessionCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.RemovePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdatePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.entities.PlantObservation;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantObservationRepository;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantSamplingSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PlantSamplingSessionAtomicOperations {

    private final PlantSamplingSessionRepository repository;
    private final PlantObservationRepository observationRepository;

    public PlantSamplingSessionAtomicOperations(
            PlantSamplingSessionRepository repository,
            PlantObservationRepository observationRepository) {
        this.repository = repository;
        this.observationRepository = observationRepository;
    }

    @Transactional
    public Long create(CreatePlantSamplingSessionCommand command) {
        var session = new PlantSamplingSession(command);
        repository.save(session);
        return session.getId();
    }

    @Transactional
    public PlantObservation addObservation(Long sessionId, AddPlantObservationCommand command) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("PlantSamplingSession not found"));

        var observation = session.addObservation(command);

        var savedObservation = observationRepository.saveAndFlush(observation);
        repository.saveAndFlush(session);
        return savedObservation;
    }

    @Transactional
    public void updateObservation(Long sessionId, UpdatePlantObservationCommand command) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("PlantSamplingSession not found"));

        session.updateObservation(command);

        repository.save(session);
    }

    @Transactional
    public void removeObservation(Long sessionId, RemovePlantObservationCommand command) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("PlantSamplingSession not found"));

        session.removeObservation(command);

        repository.save(session);
    }
}
