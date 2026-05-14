package com.floweytech.agrotrack.monitoringservice.application.internal.queryservices;


import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.EnvironmentReading;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsByPlotIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetEnvironmentReadingByIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.services.EnvironmentReadingQueryService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.EnvironmentReadingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Environment Reading Query Service Implementation
 */
@Service
public class EnvironmentReadingQueryServiceImpl implements EnvironmentReadingQueryService {
    private final EnvironmentReadingRepository environmentReadingRepository;

    /**
     * Constructor
     * @param environmentReadingRepository The {@link EnvironmentReadingRepository} instance
     */
    public EnvironmentReadingQueryServiceImpl(EnvironmentReadingRepository environmentReadingRepository){
        this.environmentReadingRepository = environmentReadingRepository;
    }

    @Override
    public Optional<EnvironmentReading> handle(GetEnvironmentReadingByIdQuery query) {
        return environmentReadingRepository.findById(query.environmentReadingId());
    }

    @Override
    public List<EnvironmentReading> handle(GetAllEnvironmentReadingsQuery query) {
        return environmentReadingRepository.findAll();
    }

    @Override
    public List<EnvironmentReading> handle(GetAllEnvironmentReadingsByPlotIdQuery query) {
        return environmentReadingRepository.findAllByPlotId(query.plotId());
    }
}
