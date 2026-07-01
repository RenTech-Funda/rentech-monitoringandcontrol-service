package com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
public class ExternalPlotService {

    private final RestClient restClient;

    public ExternalPlotService(
            @Value("${services.organization.url:http://localhost:8083}") String organizationServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(organizationServiceUrl)
                .build();
    }

    public PlotResource getPlotById(Long plotId) {
        return restClient.get()
                .uri("/api/v1/plots/{plotId}", plotId)
                .headers(headers -> currentAuthorizationHeader()
                        .ifPresent(value -> headers.set(HttpHeaders.AUTHORIZATION, value)))
                .retrieve()
                .body(PlotResource.class);
    }

    private Optional<String> currentAuthorizationHeader() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            var authorization = servletRequestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null && !authorization.isBlank()) {
                return Optional.of(authorization);
            }
        }
        return Optional.empty();
    }
}
