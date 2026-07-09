package com.floweytech.agrotrack.monitoringservice.shared.infrastructure.resilience;

import com.floweytech.agrotrack.monitoringservice.shared.domain.model.exceptions.ExternalWeatherServiceUnavailableException;
import com.floweytech.agrotrack.monitoringservice.shared.domain.model.exceptions.InvalidWeatherLocationException;
import com.floweytech.agrotrack.monitoringservice.shared.domain.model.exceptions.WeatherServiceConfigurationException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenWeatherCircuitBreakerConfiguration {

    @Bean
    public CircuitBreaker openWeatherCircuitBreaker(
            @Value("${resilience.openweather.sliding-window-size:5}") int slidingWindowSize,
            @Value("${resilience.openweather.minimum-number-of-calls:3}") int minimumNumberOfCalls,
            @Value("${resilience.openweather.failure-rate-threshold:50}") float failureRateThreshold,
            @Value("${resilience.openweather.wait-duration-in-open-state-seconds:30}") long waitDurationInOpenStateSeconds,
            @Value("${resilience.openweather.permitted-calls-in-half-open-state:2}") int permittedCallsInHalfOpenState,
            @Value("${resilience.openweather.slow-call-duration-seconds:5}") long slowCallDurationSeconds,
            @Value("${resilience.openweather.slow-call-rate-threshold:50}") float slowCallRateThreshold) {

        var config = CircuitBreakerConfig.custom()
                .slidingWindowSize(slidingWindowSize)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenStateSeconds))
                .permittedNumberOfCallsInHalfOpenState(permittedCallsInHalfOpenState)
                .slowCallDurationThreshold(Duration.ofSeconds(slowCallDurationSeconds))
                .slowCallRateThreshold(slowCallRateThreshold)
                .recordExceptions(ExternalWeatherServiceUnavailableException.class)
                .ignoreExceptions(
                        InvalidWeatherLocationException.class,
                        WeatherServiceConfigurationException.class,
                        IllegalArgumentException.class)
                .build();

        return CircuitBreakerRegistry.of(config).circuitBreaker("openWeather");
    }
}
