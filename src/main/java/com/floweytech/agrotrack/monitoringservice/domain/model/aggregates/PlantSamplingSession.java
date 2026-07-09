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
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.SampleAverage;
import com.floweytech.agrotrack.monitoringservice.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class PlantSamplingSession extends AuditableAbstractAggregateRoot<PlantSamplingSession> {

    @Embedded
    @Getter
    private PlotId plotId;

    @Column(nullable = false)
    @Getter
    private LocalDateTime sampledAt;

    @OneToMany(mappedBy = "plantSamplingSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Getter
    private List<PlantObservation> observations = new ArrayList<>();

    @Embedded
    @Getter
    private SampleAverage average;

    protected  PlantSamplingSession() {}

    public PlantSamplingSession(CreatePlantSamplingSessionCommand command) {
        this.plotId = command.plotId();
        this.sampledAt = command.sampledAt();
        this.average = SampleAverage.empty();

        this.registerEvent(new PlantSamplingSessionCreatedEvent(
                this, this.getId()
        ));
    }

    public PlantObservation addObservation(AddPlantObservationCommand command) {
        var obs = new PlantObservation(this, command.observationData());
        this.observations.add(obs);

        recomputeAverage();

        this.registerEvent(new PlantObservationAddedEvent(
                this,
                this.getId(),
                obs.getId()
        ));

        return obs;
    }


    public void updateObservation(UpdatePlantObservationCommand command) {
        var obs = findObservation(command.observationId())
                .orElseThrow(() -> new IllegalArgumentException("Observation not found"));

        obs.update(command.observationData());
        recomputeAverage();

        this.registerEvent(new PlantObservationUpdatedEvent(
                this,
                this.getId(),
                obs.getId()
        ));
    }


    public void removeObservation(RemovePlantObservationCommand command) {
        var obs = findObservation(command.observationId())
                .orElseThrow(() -> new IllegalArgumentException("Observation not found"));

        this.observations.remove(obs);
        obs.detachFromSession();
        recomputeAverage();

        this.registerEvent(new PlantObservationRemovedEvent(
                this,
                this.getId(),
                obs.getId()
        ));
    }


    private Optional<PlantObservation> findObservation(Long id) {
        return observations.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst();
    }

    private void recomputeAverage() {
        if (observations.isEmpty()) {
            this.average = SampleAverage.empty();
            return;
        }

        double heightSum = 0;
        int leafSum = 0;
        int fruitSum = 0;

        for (var obs : observations) {
            heightSum += obs.getPlantObservationData().heightCm();
            leafSum += obs.getPlantObservationData().leafCount();
            fruitSum += obs.getPlantObservationData().fruitCount();
        }

        int n = observations.size();
        this.average = new SampleAverage(
                heightSum / n,
                (double) leafSum / n,
                (double) fruitSum / n
        );
    }
}
