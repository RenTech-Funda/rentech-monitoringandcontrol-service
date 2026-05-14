package com.floweytech.agrotrack.monitoringservice.domain.model.queries;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;

public record GetPlantSamplingSessionsByPlotIdQuery(PlotId plotId) {}
