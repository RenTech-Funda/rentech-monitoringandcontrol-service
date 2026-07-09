package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

import java.util.List;

public record PlotRecommendationsResource(
        Long plotId,
        String plotName,
        String riskLevel,
        String summary,
        List<RecommendationResource> recommendations,
        List<SuggestedTaskResource> suggestedTasks
) {
}
