
package com.floweytech.agrotrack.monitoringservice.domain.model.aggregates;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreateEnvironmentReadingCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingValue;
import com.floweytech.agrotrack.monitoringservice.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * EnvironmentReading aggregate root
 * @summary
 * The EnvironmentReading class is an aggregate root that represents an environmental reading taken from a specific plot.
 * It is responsible for the handling the CreateEnvironmentReadingCommand command.
 */
@Entity
public class EnvironmentReading extends AuditableAbstractAggregateRoot<EnvironmentReading> {

    @Embedded
    @Getter
    private PlotId plotId;

    @Embedded
    @Getter
    private ReadingValue readingValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    private ReadingType type;

    @Column(nullable = false)
    @Getter
    private LocalDateTime measuredAt;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    protected EnvironmentReading() {}

    /**
     * Creates a new EnvironmentReading aggregate based on the CreateEnvironmentReadingCommand.
     * @param command - the CreateEnvironmentReadingCommand command.
     */
    public EnvironmentReading(CreateEnvironmentReadingCommand command) {
        this.plotId = command.plotId();
        this.type = command.type();
        this.readingValue = command.readingValue();
        this.measuredAt = command.measuredAt();
    }




}