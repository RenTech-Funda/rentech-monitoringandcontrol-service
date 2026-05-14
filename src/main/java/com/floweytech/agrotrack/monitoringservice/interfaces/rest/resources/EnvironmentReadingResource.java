package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;

import java.time.LocalDateTime;

/**
 *Resource record for a  environment reading
 * @summary
 * This record represents an environmental reading resource.
 * It contains the plot ID, reading type, value, unit, and the timestamp of when the reading was measured.
 */


public record EnvironmentReadingResource(
        Long plotId,
        ReadingType type,
        Double value,
        String unit,
        LocalDateTime measuredAt) {
}
