package com.floweytech.agrotrack.monitoringservice.domain.model.entities;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreatePlantSamplingSessionCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlantObservationData;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PlantObservationTest {

    @Test
    void shouldCreatePlantObservationSuccessfully() {
        // Arrange
        PlantSamplingSession session = new PlantSamplingSession(
                new CreatePlantSamplingSessionCommand(new PlotId(1L), LocalDateTime.now())
        );
        PlantObservationData data = new PlantObservationData(15.5, 10, 2, "Planta saludable");

        // Act
        PlantObservation observation = new PlantObservation(session, data);

        // Assert
        assertNotNull(observation);
        assertEquals(data, observation.getPlantObservationData());
        assertEquals(session, ReflectionTestUtils.getField(observation, "plantSamplingSession"));
    }

    @Test
    void shouldUpdateObservationDataSuccessfully() {
        // Arrange
        PlantSamplingSession session = new PlantSamplingSession(
                new CreatePlantSamplingSessionCommand(new PlotId(1L), LocalDateTime.now())
        );
        PlantObservation observation = new PlantObservation(
                session, new PlantObservationData(10.0, 5, 1, "Inicial")
        );
        PlantObservationData newData = new PlantObservationData(20.0, 12, 4, "Actualizado");

        // Act
        observation.update(newData);

        // Assert
        assertEquals(newData, observation.getPlantObservationData());
        assertEquals(20.0, observation.getPlantObservationData().heightCm());
    }

    @Test
    void shouldThrowExceptionWhenDataIsInvalid() {
        // Arrange
        PlantSamplingSession session = new PlantSamplingSession(
                new CreatePlantSamplingSessionCommand(new PlotId(1L), LocalDateTime.now())
        );
        // Altura negativa o cero no está permitida por las reglas de negocio en applyData
        PlantObservationData invalidData = new PlantObservationData(-5.0, 10, 2, "Invalido");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new PlantObservation(session, invalidData));
    }

    @Test
    void shouldDetachFromSessionSuccessfully() {
        // Arrange
        PlantSamplingSession session = new PlantSamplingSession(
                new CreatePlantSamplingSessionCommand(new PlotId(1L), LocalDateTime.now())
        );
        PlantObservation observation = new PlantObservation(
                session, new PlantObservationData(10.0, 5, 1, "Normal")
        );

        // Act
        observation.detachFromSession();

        // Assert
        assertNull(ReflectionTestUtils.getField(observation, "plantSamplingSession"),
                "La sesión asociada debería ser nula después del detach");
    }
}