package com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantSamplingSessionRepository extends JpaRepository<PlantSamplingSession,Long> {
    List<PlantSamplingSession> findAllByPlotId(PlotId plotId);
}
