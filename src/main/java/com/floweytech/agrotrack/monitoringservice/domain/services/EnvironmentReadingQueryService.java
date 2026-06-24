package com.floweytech.agrotrack.monitoringservice.domain.services;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.EnvironmentReading;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsByPlotIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetAllEnvironmentReadingsQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetEnvironmentReadingByIdQuery;
import com.floweytech.agrotrack.monitoringservice.domain.model.queries.GetEnvironmentReadingsByPlotIdAndTypeAndPeriodQuery;

import java.util.List;
import java.util.Optional;

public interface EnvironmentReadingQueryService {

    Optional<EnvironmentReading> handle(GetEnvironmentReadingByIdQuery query);

    List<EnvironmentReading> handle(GetAllEnvironmentReadingsQuery query);

    List<EnvironmentReading> handle(GetAllEnvironmentReadingsByPlotIdQuery query);

    // Nuevo: usado por el reporting-service vía HTTP
    List<EnvironmentReading> handle(GetEnvironmentReadingsByPlotIdAndTypeAndPeriodQuery query);
}