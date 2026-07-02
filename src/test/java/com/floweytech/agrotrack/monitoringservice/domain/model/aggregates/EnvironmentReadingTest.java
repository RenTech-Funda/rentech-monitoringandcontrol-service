package com.floweytech.agrotrack.monitoringservice.domain.model.aggregates;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreateEnvironmentReadingCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingValue;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentReadingTest {

    @Test
    void shouldCreateEnvironmentReadingFromCommandSuccessfully() {
        // Arrange
        PlotId plotId = new PlotId(10L);
        ReadingType type = ReadingType.TEMPERATURE;
        ReadingValue readingValue = new ReadingValue(24.5, "Celsius");
        LocalDateTime measuredAt = LocalDateTime.of(2026, 7, 2, 14, 30);

        CreateEnvironmentReadingCommand command = new CreateEnvironmentReadingCommand(
                plotId, type, readingValue, measuredAt
        );

        // Act
        EnvironmentReading reading = new EnvironmentReading(command);

        // Assert
        assertNotNull(reading);
        assertEquals(plotId, reading.getPlotId());
        assertEquals(type, reading.getType());
        assertEquals(readingValue, reading.getReadingValue());
        assertEquals(measuredAt, reading.getMeasuredAt());
    }
}