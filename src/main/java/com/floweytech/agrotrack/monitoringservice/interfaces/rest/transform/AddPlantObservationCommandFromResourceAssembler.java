package com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.AddPlantObservationCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlantObservationData;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.AddPlantObservationResource;

public class AddPlantObservationCommandFromResourceAssembler {

    /**
     * Converts AddPlantObservationResource to AddPlantObservationCommand.
     */
    public static AddPlantObservationCommand toCommandFromResource(AddPlantObservationResource resource) {

        var data = new PlantObservationData(
                resource.heightCm(),
                resource.leafCount(),
                resource.fruitCount(),
                resource.notes()
        );

        return new AddPlantObservationCommand(data);
    }
}
