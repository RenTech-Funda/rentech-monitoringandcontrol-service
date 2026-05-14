package com.floweytech.agrotrack.monitoringservice.application.internal.commandservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.EnvironmentReading;
import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreateEnvironmentReadingCommand;
import com.floweytech.agrotrack.monitoringservice.domain.services.EnvironmentReadingCommandService;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.EnvironmentReadingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * EnvironmentReading Command Service Implementation
 */
@Service
public class EnvironmentReadingCommandServicelmlp implements EnvironmentReadingCommandService {
    private final EnvironmentReadingRepository environmentRepository;

    /**
     * Constructor
     * @param environmentRepository The {@link EnvironmentReadingRepository } instance
     */
    public EnvironmentReadingCommandServicelmlp(EnvironmentReadingRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
    }

    @Override
    public Optional<EnvironmentReading> handle(CreateEnvironmentReadingCommand command) {
        var environmentReading = new EnvironmentReading(command);
        environmentRepository.save(environmentReading);
        return Optional.of(environmentReading);
    }
}
