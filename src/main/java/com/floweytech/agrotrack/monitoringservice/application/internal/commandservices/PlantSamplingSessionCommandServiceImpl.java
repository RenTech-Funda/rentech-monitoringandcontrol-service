package com.floweytech.agrotrack.monitoringservice.application.internal.commandservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.AddPlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreatePlantSamplingSessionCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.RemovePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdatePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.entities.PlantObservation;
import com.floweytech.agrotrack.monitoringservice.domain.services.PlantSamplingSessionCommandService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantObservationRepository;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantSamplingSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PlantSamplingSessionCommandServiceImpl implements PlantSamplingSessionCommandService {

    private final PlantSamplingSessionRepository repository;
    private final PlantObservationRepository observationRepository;

    public PlantSamplingSessionCommandServiceImpl(
            PlantSamplingSessionRepository repository,
            PlantObservationRepository observationRepository) {
        this.repository = repository;
        this.observationRepository = observationRepository;
    }

    @Override
    @Transactional
    public Long handle(CreatePlantSamplingSessionCommand command) {
        var session = new PlantSamplingSession(command);
        repository.save(session);
        return session.getId();
    }

    @Override
    @Transactional
    public PlantObservation handle(Long sessionId, AddPlantObservationCommand command) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("PlantSamplingSession not found"));

        var observation = session.addObservation(command);

        var savedObservation = observationRepository.saveAndFlush(observation);
        repository.saveAndFlush(session);
        return savedObservation;
    }

    @Override
    @Transactional
    public void handle(Long sessionId, UpdatePlantObservationCommand command) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("PlantSamplingSession not found"));

        session.updateObservation(command);

        repository.save(session);
    }

    @Override
    @Transactional
    public void handle(Long sessionId, RemovePlantObservationCommand command) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("PlantSamplingSession not found"));

        session.removeObservation(command);

        repository.save(session);
    }
}
