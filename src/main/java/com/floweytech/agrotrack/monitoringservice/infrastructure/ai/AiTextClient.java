package com.floweytech.agrotrack.monitoringservice.infrastructure.ai;

import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.RecommendationResource;

import java.util.List;

public interface AiTextClient {
    String summarizeHealth(String context, String fallback);

    String summarizeRecommendations(String context, List<RecommendationResource> recommendations, String fallback);
}
