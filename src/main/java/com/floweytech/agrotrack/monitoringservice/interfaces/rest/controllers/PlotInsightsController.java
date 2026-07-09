package com.floweytech.agrotrack.monitoringservice.interfaces.rest.controllers;

import com.floweytech.agrotrack.monitoringservice.application.internal.insights.PlotInsightService;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.PlotHealthSummaryResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.PlotRecommendationsResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/monitoring/plots", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Plot Insights", description = "Smart plot health and recommendation endpoints")
public class PlotInsightsController {

    private final PlotInsightService plotInsightService;

    public PlotInsightsController(PlotInsightService plotInsightService) {
        this.plotInsightService = plotInsightService;
    }

    @GetMapping("/{plotId}/health-summary")
    public ResponseEntity<PlotHealthSummaryResource> getHealthSummary(@PathVariable Long plotId) {
        return ResponseEntity.ok(plotInsightService.getHealthSummary(plotId));
    }

    @GetMapping("/{plotId}/recommendations")
    public ResponseEntity<PlotRecommendationsResource> getRecommendations(@PathVariable Long plotId) {
        return ResponseEntity.ok(plotInsightService.getRecommendations(plotId));
    }
}
