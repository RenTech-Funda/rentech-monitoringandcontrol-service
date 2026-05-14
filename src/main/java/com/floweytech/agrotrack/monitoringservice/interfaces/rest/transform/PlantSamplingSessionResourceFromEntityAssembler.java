package com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.PlantSamplingSession;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.PlantSamplingSessionResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.SampleAverageResource;

import java.util.stream.Collectors;

public class PlantSamplingSessionResourceFromEntityAssembler {

    /**
     * Converts PlantSamplingSession entity to PlantSamplingSessionResource.
     */
    public static PlantSamplingSessionResource toResourceFromEntity(PlantSamplingSession entity) {

        var avg = entity.getAverage();
        var avgResource = new SampleAverageResource(
                avg.getAvgHeightCm(),
                avg.getAvgLeafCount(),
                avg.getAvgFruitCount()
        );

        var observations = entity.getObservations()
                .stream()
                .map(PlantObservationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return new PlantSamplingSessionResource(
                entity.getId(),
                entity.getPlotId().plotId(),
                entity.getSampledAt(),
                avgResource,
                observations
        );
    }
}
