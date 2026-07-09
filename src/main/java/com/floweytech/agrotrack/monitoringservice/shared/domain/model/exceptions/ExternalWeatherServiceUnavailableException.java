package com.floweytech.agrotrack.monitoringservice.shared.domain.model.exceptions;

public class ExternalWeatherServiceUnavailableException extends RuntimeException {

    public ExternalWeatherServiceUnavailableException(String message) {
        super(message);
    }

    public ExternalWeatherServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
