
package com.floweytech.agrotrack.monitoringservice.domain.model.commands;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingValue;

import java.time.LocalDateTime;

/**
 * CreateEnvironmentReadingCommand
 * @summary
 * CreateEnvironmentReadingCommand is a record class that represents the action of creating a new environmental reading.
 * It contains the necessary information to register a reading from a plot.
 */
public record CreateEnvironmentReadingCommand(
        PlotId plotId,
        ReadingType type,
        ReadingValue readingValue,
        LocalDateTime measuredAt
) {
    /**
     * Validates the command.
     * @throws IllegalArgumentException if any of the required fields are null or invalid.
     */
    public CreateEnvironmentReadingCommand {
        if (plotId == null)
            throw new IllegalArgumentException("plotId cannot be null");
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");
        if (readingValue == null)
            throw new IllegalArgumentException("readingValue cannot be null");
        if (measuredAt == null)
            throw new IllegalArgumentException("measuredAt cannot be null");
    }

}
