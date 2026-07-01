package com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OpenWeatherService {

    private final String apiKey;
    private final RestClient restClient;

    public OpenWeatherService(
            @Value("${openweather.api.key:}") String apiKey,
            @Value("${openweather.api.url:https://api.openweathermap.org}") String openWeatherApiUrl) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(openWeatherApiUrl)
                .build();
    }

    public OpenWeatherCurrentWeatherResource getCurrentWeatherByLocation(String location) {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("OPENWEATHER_API_KEY is not configured");
        }
        if (!StringUtils.hasText(location)) {
            throw new IllegalArgumentException("Plot location is required to fetch weather data");
        }

        var coordinates = geocode(location);

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather")
                        .queryParam("lat", coordinates.lat())
                        .queryParam("lon", coordinates.lon())
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .body(OpenWeatherCurrentWeatherResource.class);
    }

    private OpenWeatherGeoResource geocode(String location) {
        List<OpenWeatherGeoResource> results = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geo/1.0/direct")
                        .queryParam("q", location)
                        .queryParam("limit", 1)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("OpenWeather could not resolve location: " + location);
        }

        var result = results.getFirst();
        if (result.lat() == null || result.lon() == null) {
            throw new IllegalArgumentException("OpenWeather returned invalid coordinates for location: " + location);
        }
        return result;
    }
}
