package com.floweytech.agrotrack.monitoringservice.application.internal.queryservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.domain.model.entities.PlantObservation;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllPlantSamplingSessionsQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetObservationsBySessionIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetPlantSamplingSessionByIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetPlantSamplingSessionsByPlotIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.services.PlantSamplingSessionQueryService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.PlantSamplingSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantSamplingSessionQueryServiceImpl implements PlantSamplingSessionQueryService {

    private final PlantSamplingSessionRepository repository;

    public PlantSamplingSessionQueryServiceImpl(PlantSamplingSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PlantSamplingSession> handle(GetPlantSamplingSessionByIdQuery query) {
        return repository.findById(query.sessionId());
    }

    @Override
    public List<PlantSamplingSession> handle(GetAllPlantSamplingSessionsQuery query) {
        return repository.findAll();
    }

    @Override
    public List<PlantSamplingSession> handle(GetPlantSamplingSessionsByPlotIdQuery query) {
        return repository.findAllByPlotId(query.plotId());
    }

    @Override
    public List<PlantObservation> handle(GetObservationsBySessionIdQuery query) {
        return repository.findById(query.sessionId())
                .map(PlantSamplingSession::getObservations)
                .orElse(List.of());
    }
}
