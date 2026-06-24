package com.floweytech.agrotrack.monitoringservice.application.internal.queryservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.EnvironmentReading;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsByPlotIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetEnvironmentReadingByIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetEnvironmentReadingsByPlotIdAndTypeAndPeriodQuery;
import com.floweytech.agrotrack.monitoringservice.domain.services.EnvironmentReadingQueryService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.EnvironmentReadingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnvironmentReadingQueryServiceImpl implements EnvironmentReadingQueryService {

    private final EnvironmentReadingRepository environmentReadingRepository;

    public EnvironmentReadingQueryServiceImpl(EnvironmentReadingRepository environmentReadingRepository) {
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

    @Override
    public List<EnvironmentReading> handle(GetEnvironmentReadingsByPlotIdAndTypeAndPeriodQuery query) {
        return environmentReadingRepository.findByPlotIdAndTypeAndMeasuredAtBetween(
                query.plotId(),
                query.readingType(),
                query.start(),
                query.end()
        );
    }
}