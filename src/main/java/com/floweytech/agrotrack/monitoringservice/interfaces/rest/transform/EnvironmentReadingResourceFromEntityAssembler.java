package com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.EnvironmentReading;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.EnvironmentReadingResource;

/**
 * Assembler to create a EnvironmentReadingResource from an EnvironmentReading entity.
 */
public class EnvironmentReadingResourceFromEntityAssembler {
    /**
     * Converts an EnvironmentReading entity to an EnvironmentReadingResource.
     * @param entity EnvironmentReading entity to convert
     * @return EnvironmentReadingResource created from the entity
     */
    public static EnvironmentReadingResource toResourceFromEntity( EnvironmentReading entity ){
        return new EnvironmentReadingResource(
                entity.getPlotId().plotId(),
                entity.getType(),
                entity.getReadingValue().getValue(),
                entity.getReadingValue().getUnit(),
                entity.getMeasuredAt()
        );
    }
}
