package com.floweytech.agrotrack.monitoringservice.domain.model.aggregates;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.AddPlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreatePlantSamplingSessionCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.RemovePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdatePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.entities.PlantObservation;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.PlantObservationAddedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.PlantObservationRemovedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.PlantObservationUpdatedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.events.PlantSamplingSessionCreatedEvent;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlantObservationData;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class PlantSamplingSessionTest {

    @Test
    void shouldCreateSessionAndRegisterCreatedEventSuccessfully() {
        // Arrange
        PlotId plotId = new PlotId(5L);
        LocalDateTime sampledAt = LocalDateTime.now();
        CreatePlantSamplingSessionCommand command = new CreatePlantSamplingSessionCommand(plotId, sampledAt);

        // Act
        PlantSamplingSession session = new PlantSamplingSession(command);

        // Assert
        assertEquals(plotId, session.getPlotId());
        assertEquals(sampledAt, session.getSampledAt());
        assertNotNull(session.getAverage());
        assertEquals(0.0, session.getAverage().getAvgHeightCm());
        assertTrue(session.getObservations().isEmpty());

        // Verificamos registro del evento
        Collection<Object> events = (Collection<Object>) ReflectionTestUtils.getField(session, "domainEvents");
        assertNotNull(events);
        assertTrue(events.stream().anyMatch(e -> e instanceof PlantSamplingSessionCreatedEvent));
    }

    @Test
    void shouldAddObservationRecomputeAverageAndRegisterEventSuccessfully() {
        // Arrange
        PlantSamplingSession session = new PlantSamplingSession(
                new CreatePlantSamplingSessionCommand(new PlotId(1L), LocalDateTime.now())
        );
        PlantObservationData data1 = new PlantObservationData(20.0, 10, 4, "Obs 1");
        PlantObservationData data2 = new PlantObservationData(30.0, 20, 6, "Obs 2");

        // Act
        session.addObservation(new AddPlantObservationCommand(data1));
        session.addObservation(new AddPlantObservationCommand(data2));

        // Assert
        assertEquals(2, session.getObservations().size());
        // Promedio esperado: Altura (20+30)/2 = 25.0 | Hojas (10+20)/2 = 15.0 | Frutos (4+6)/2 = 5.0
        assertEquals(25.0, session.getAverage().getAvgHeightCm());
        assertEquals(15.0, session.getAverage().getAvgLeafCount());
        assertEquals(5.0, session.getAverage().getAvgFruitCount());

        Collection<Object> events = (Collection<Object>) ReflectionTestUtils.getField(session, "domainEvents");
        assertTrue(events.stream().anyMatch(e -> e instanceof PlantObservationAddedEvent));
    }

    @Test
    void shouldUpdateObservationAndRecomputeAverageSuccessfully() {
        // Arrange
        PlantSamplingSession session = new PlantSamplingSession(
                new CreatePlantSamplingSessionCommand(new PlotId(1L), LocalDateTime.now())
        );
        session.addObservation(new AddPlantObservationCommand(new PlantObservationData(10.0, 4, 2, "Inicial")));

        // Simulamos que JPA le asignó el ID 100L a la observación recién creada
        PlantObservation createdObs = session.getObservations().get(0);
        ReflectionTestUtils.setField(createdObs, "id", 100L);

        PlantObservationData updatedData = new PlantObservationData(50.0, 10, 8, "Modificado");
        UpdatePlantObservationCommand updateCommand = new UpdatePlantObservationCommand(100L, updatedData);

        // Act
        session.updateObservation(updateCommand);

        // Assert
        assertEquals(50.0, session.getAverage().getAvgHeightCm());
        assertEquals(10.0, session.getAverage().getAvgLeafCount());
        assertEquals(8.0, session.getAverage().getAvgFruitCount());

        Collection<Object> events = (Collection<Object>) ReflectionTestUtils.getField(session, "domainEvents");
        assertTrue(events.stream().anyMatch(e -> e instanceof PlantObservationUpdatedEvent));
    }

    @Test
    void shouldRemoveObservationAndRecomputeAverageSuccessfully() {
        // Arrange
        PlantSamplingSession session = new PlantSamplingSession(
                new CreatePlantSamplingSessionCommand(new PlotId(1L), LocalDateTime.now())
        );
        session.addObservation(new AddPlantObservationCommand(new PlantObservationData(20.0, 10, 4, "A borrar")));
        session.addObservation(new AddPlantObservationCommand(new PlantObservationData(40.0, 20, 8, "Se queda")));

        // Simulamos IDs de base de datos
        ReflectionTestUtils.setField(session.getObservations().get(0), "id", 1L);
        ReflectionTestUtils.setField(session.getObservations().get(1), "id", 2L);

        RemovePlantObservationCommand removeCommand = new RemovePlantObservationCommand(1L);

        // Act
        session.removeObservation(removeCommand);

        // Assert
        assertEquals(1, session.getObservations().size());
        assertEquals(40.0, session.getAverage().getAvgHeightCm(), "El promedio debe recalcularse con la observación restante");

        Collection<Object> events = (Collection<Object>) ReflectionTestUtils.getField(session, "domainEvents");
        assertTrue(events.stream().anyMatch(e -> e instanceof PlantObservationRemovedEvent));
    }
}