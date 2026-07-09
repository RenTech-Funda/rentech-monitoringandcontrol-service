package com.floweytech.agrotrack.monitoringservice.application.internal.commandservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.AddPlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreatePlantSamplingSessionCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.RemovePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.UpdatePlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.services.PlantSamplingSessionCommandService;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class PlantSamplingSessionCommandServiceImpl implements PlantSamplingSessionCommandService {

    private final PlantSamplingSessionAtomicOperations atomicOperations;

    public PlantSamplingSessionCommandServiceImpl(PlantSamplingSessionAtomicOperations atomicOperations) {
        this.atomicOperations = atomicOperations;
    }

    @Override
    public Long handle(CreatePlantSamplingSessionCommand command) {
        return atomicOperations.create(command);
    }

    @Override
    @Retryable(
            retryFor = {CannotAcquireLockException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public Long handle(Long sessionId, AddPlantObservationCommand command) {
        return atomicOperations.addObservation(sessionId, command);
    }

    @Override
    @Retryable(
            retryFor = {CannotAcquireLockException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public void handle(Long sessionId, UpdatePlantObservationCommand command) {
        atomicOperations.updateObservation(sessionId, command);
    }

    @Override
    @Retryable(
            retryFor = {CannotAcquireLockException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public void handle(Long sessionId, RemovePlantObservationCommand command) {
        atomicOperations.removeObservation(sessionId, command);
    }
}