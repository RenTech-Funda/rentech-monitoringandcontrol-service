package com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices;

import com.floweytech.agrotrack.monitoringservice.shared.domain.model.exceptions.ExternalWeatherServiceUnavailableException;
import com.floweytech.agrotrack.monitoringservice.shared.domain.model.exceptions.InvalidWeatherLocationException;
import com.floweytech.agrotrack.monitoringservice.shared.domain.model.exceptions.WeatherServiceConfigurationException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;

@Service
public class OpenWeatherService {

    private final String apiKey;
    private final RestClient restClient;
    private final CircuitBreaker openWeatherCircuitBreaker;

    public OpenWeatherService(
            @Value("${openweather.api.key:}") String apiKey,
            @Value("${openweather.api.url:https://api.openweathermap.org}") String openWeatherApiUrl,
            @Value("${openweather.connect-timeout-seconds:3}") long connectTimeoutSeconds,
            @Value("${openweather.read-timeout-seconds:5}") long readTimeoutSeconds,
            CircuitBreaker openWeatherCircuitBreaker) {
        this.apiKey = apiKey;
        this.openWeatherCircuitBreaker = openWeatherCircuitBreaker;

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        this.restClient = RestClient.builder()
                .baseUrl(openWeatherApiUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public OpenWeatherCurrentWeatherResource getCurrentWeatherByLocation(String location) {
        if (!StringUtils.hasText(apiKey)) {
            throw new WeatherServiceConfigurationException("OPENWEATHER_API_KEY is not configured");
        }
        if (!StringUtils.hasText(location)) {
            throw new InvalidWeatherLocationException("Plot location is required to fetch weather data");
        }

        try {
            return openWeatherCircuitBreaker.executeSupplier(() -> fetchCurrentWeatherByLocation(location));
        } catch (CallNotPermittedException ex) {
            throw new ExternalWeatherServiceUnavailableException(
                    "OpenWeather service is temporarily unavailable. Please try again later.", ex);
        }
    }

    private OpenWeatherCurrentWeatherResource fetchCurrentWeatherByLocation(String location) {
        try {
            var coordinates = geocode(location);

            var weather = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/2.5/weather")
                            .queryParam("lat", coordinates.lat())
                            .queryParam("lon", coordinates.lon())
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .retrieve()
                    .body(OpenWeatherCurrentWeatherResource.class);

            if (weather == null || weather.main() == null) {
                throw new ExternalWeatherServiceUnavailableException("OpenWeather returned an empty weather response");
            }

            return weather;
        } catch (InvalidWeatherLocationException | WeatherServiceConfigurationException |
                 ExternalWeatherServiceUnavailableException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ExternalWeatherServiceUnavailableException(
                    "OpenWeather service is temporarily unavailable. Please try again later.", ex);
        }
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
            throw new InvalidWeatherLocationException("OpenWeather could not resolve location: " + location);
        }

        var result = results.getFirst();
        if (result.lat() == null || result.lon() == null) {
            throw new InvalidWeatherLocationException("OpenWeather returned invalid coordinates for location: " + location);
        }
        return result;
    }
}
