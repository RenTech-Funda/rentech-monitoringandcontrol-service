package com.floweytech.agrotrack.monitoringservice.domain.services;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.AddPlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreatePlantSamplingSessionCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.RemovePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdatePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.entities.PlantObservation;

public interface PlantSamplingSessionCommandService {

    Long handle(CreatePlantSamplingSessionCommand command);

    PlantObservation handle(Long sessionId, AddPlantObservationCommand command);

    void handle(Long sessionId, UpdatePlantObservationCommand command);

    void handle(Long sessionId, RemovePlantObservationCommand command);
}
