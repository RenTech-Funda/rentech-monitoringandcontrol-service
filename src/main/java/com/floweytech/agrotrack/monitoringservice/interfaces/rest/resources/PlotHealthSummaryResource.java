package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

import java.util.List;

public record PlotHealthSummaryResource(
        Long plotId,
        String plotName,
        Long organizationId,
        Integer healthScore,
        String status,
        String summary,
        List<String> mainProblems,
        List<AlertResource> alerts
) {
}
