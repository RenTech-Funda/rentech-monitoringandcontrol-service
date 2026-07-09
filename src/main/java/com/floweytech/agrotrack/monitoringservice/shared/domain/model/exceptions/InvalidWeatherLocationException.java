package com.floweytech.agrotrack.monitoringservice.shared.domain.model.exceptions;

public class InvalidWeatherLocationException extends IllegalArgumentException {

    public InvalidWeatherLocationException(String message) {
        super(message);
    }
}
