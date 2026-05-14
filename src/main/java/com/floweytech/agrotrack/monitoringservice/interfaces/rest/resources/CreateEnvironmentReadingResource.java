package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;

import java.time.LocalDateTime;

/**
 * CreateEnvironmentReadingResource
 * @summary
 * Resource representing the data transfer object for creating an environmental reading.
 * Contains primitive types that will be converted to domain objects.
 */
public record CreateEnvironmentReadingResource(
        Long plotId,
        ReadingType type,
        Double value,
        String unit,
        LocalDateTime measuredAt
) {
    /**
     * Compact constructor for validation.
     * @throws IllegalArgumentException if any field is invalid.
     */
    public CreateEnvironmentReadingResource {
        if (plotId == null || plotId <= 0)
            throw new IllegalArgumentException("plotId cannot be null or less than or equal to zero");
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");
        if (value == null)
            throw new IllegalArgumentException("value cannot be null");
        if (unit == null || unit.isBlank())
            throw new IllegalArgumentException("unit cannot be null or blank");
        if (measuredAt == null)
            throw new IllegalArgumentException("measuredAt cannot be null");
    }
}
