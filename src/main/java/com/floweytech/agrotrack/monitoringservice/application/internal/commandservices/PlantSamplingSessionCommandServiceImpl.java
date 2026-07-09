package com.floweytech.agrotrack.monitoringservice.application.internal.commandservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.AddPlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreatePlantSamplingSessionCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.RemovePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdatePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.services.PlantSamplingSessionCommandService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantSamplingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlantSamplingSessionCommandServiceImpl implements PlantSamplingSessionCommandService {

    private final PlantSamplingSessionRepository repository;

    public PlantSamplingSessionCommandServiceImpl(PlantSamplingSessionRepository repository) {
        this.repository = repository;
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
    public Long handle(Long sessionId, AddPlantObservationCommand command) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("PlantSamplingSession not found"));

        Long obsId = session.addObservation(command);

        repository.save(session);
        return obsId;
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