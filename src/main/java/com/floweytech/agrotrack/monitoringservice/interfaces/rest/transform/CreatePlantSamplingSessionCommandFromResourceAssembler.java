package com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreatePlantSamplingSessionCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.CreatePlantSamplingSessionResource;

public class CreatePlantSamplingSessionCommandFromResourceAssembler {

    /**
     * Converts CreatePlantSamplingSessionResource to CreatePlantSamplingSessionCommand.
     */
    public static CreatePlantSamplingSessionCommand toCommandFromResource(CreatePlantSamplingSessionResource resource) {

        var plotId = new PlotId(resource.plotId());

        return new CreatePlantSamplingSessionCommand(
                plotId,
                resource.sampledAt()
        );
    }
}
