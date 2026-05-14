package com.floweytech.agrotrack.monitoringservice.domain.model.entities;


import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlantObservationData;
import com.floweytech.agrotrack.monitoringservice.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
public class PlantObservation extends AuditableModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_sampling_session_id", nullable = false)
    private PlantSamplingSession plantSamplingSession;

    @Embedded
    @Getter
    private PlantObservationData plantObservationData;

    protected PlantObservation() {
        // for JPA
    }

    public PlantObservation(PlantSamplingSession session, PlantObservationData data) {
        if (session == null)
            throw new IllegalArgumentException("session cannot be null");
        this.plantSamplingSession = session;
        applyData(data);
    }

    public void update(PlantObservationData data) {
        applyData(data);
    }

    public void detachFromSession() {
        this.plantSamplingSession = null;
    }

    private void applyData(PlantObservationData data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");

        if (data.heightCm() == null || data.heightCm() <= 0)
            throw new IllegalArgumentException("heightCm must be > 0");

        if (data.leafCount() == null || data.leafCount() < 0)
            throw new IllegalArgumentException("leafCount cannot be negative");

        if (data.fruitCount() == null || data.fruitCount() < 0)
            throw new IllegalArgumentException("fruitCount cannot be negative");

        this.plantObservationData = data;
    }
}
