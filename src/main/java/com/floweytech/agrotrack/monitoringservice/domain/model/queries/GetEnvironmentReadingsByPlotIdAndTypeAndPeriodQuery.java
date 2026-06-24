package com.floweytech.agrotrack.monitoringservice.domain.model.queries;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;

import java.time.LocalDateTime;

/**
 * Query to get all environment readings for a specific plot,
 * filtered by reading type and a date/time range.
 */
public record GetEnvironmentReadingsByPlotIdAndTypeAndPeriodQuery(
        PlotId plotId,
        ReadingType readingType,
        LocalDateTime start,
        LocalDateTime end
) {}