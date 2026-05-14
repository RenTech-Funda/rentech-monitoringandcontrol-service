package com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform;

import com.floweytech.agrotrack.monitoringservice.domain.model.commands.CreateEnvironmentReadingCommand;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingValue;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.CreateEnvironmentReadingResource;

public class CreateEnvironmentReadingCommandFromResourceAssembler {
    /**
     * Converts a CreateEnvironmentReadingResource to a CreateEnvironmentReadingResource to a CreateEnvironmentReadingCommand.
     * @param resource CreateEnvironmentReadingResource to convert
     * @return CreateEnvironmentReadingCommand created from the resource
     */
    public static CreateEnvironmentReadingCommand toCommandFromResource(CreateEnvironmentReadingResource resource){
        var plotId = new PlotId(resource.plotId());
        var readingValue = new ReadingValue(resource.value(), resource.unit());


        return new CreateEnvironmentReadingCommand(
                plotId,
                resource.type(),
                readingValue,
                resource.measuredAt()
        );
    }
}
