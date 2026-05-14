package com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects;


import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * Measurement
 * @summary
 * Value Object representing a measured value with its unit.
 * It is immutable and embedded within EnvironmentReading.
 */
@Embeddable
@Getter
public class ReadingValue {
    private Double value;
    private String unit;

    protected ReadingValue() {}

    public ReadingValue(Double value, String unit){
        if (value == null)
            throw new IllegalArgumentException("value cannot be null");
        if (unit == null || unit.isBlank())
            throw new IllegalArgumentException("unit cannot be null or blank");
        this.value = value;
        this.unit = unit;
    }

}
