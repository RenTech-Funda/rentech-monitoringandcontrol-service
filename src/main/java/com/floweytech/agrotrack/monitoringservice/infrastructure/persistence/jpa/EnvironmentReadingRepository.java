package com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.EnvironmentReading;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Repository
public interface EnvironmentReadingRepository extends JpaRepository<EnvironmentReading, Long> {

    /**
     * Find all EnvironmentReadings by PlotId
     * @param plotId The PlotId
     * @return List of EnvironmentReadings
     */
    List<EnvironmentReading> findAllByPlotId(PlotId plotId);

    /**
     * Find all EnvironmentReadings by PlotId, type ,start date and end date.
     * @param plotId
     * @param type
     * @param start
     * @param end
     * @return
     */
    List<EnvironmentReading> findByPlotIdAndTypeAndMeasuredAtBetween(PlotId plotId, ReadingType type, LocalDateTime start, LocalDateTime end);

}
