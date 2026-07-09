package com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa;

import com.floweytech.agrotrack.monitoringservice.domain.model.entities.PlantObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantObservationRepository extends JpaRepository<PlantObservation, Long> {
}
