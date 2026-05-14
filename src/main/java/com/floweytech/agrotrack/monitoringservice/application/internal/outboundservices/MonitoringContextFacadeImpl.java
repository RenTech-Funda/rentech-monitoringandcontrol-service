package com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices;

import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects.ReadingType;
import com.floweytech.agrotrack.monitoringservice.infrastructure.persistence.jpa.EnvironmentReadingRepository;
import com.floweytech.agrotrack.monitoringservice.interfaces.acl.MonitoringContextFacade;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.ReadingResource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Monitoring Context Facade Implementation
 * @summary
 * Implements the contract defined by the MonitoringContextFacade.
 * It serves as the entry point for cross-context communication (ACL), handling the logic
 * to retrieve internal domain entities (EnvironmentReadings) and transforming them
 * into shareable Data Transfer Objects (ReadingResources) for external consumers.
 *
 * @author FloweyTech developer team
 */
@Service
public class MonitoringContextFacadeImpl implements MonitoringContextFacade {

    private final EnvironmentReadingRepository readingRepository;

    public MonitoringContextFacadeImpl(EnvironmentReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    /**
     * Fetch Readings by Plot, Type, and Period
     * @summary
     * Orchestrates the retrieval of reading data. It converts the primitive input parameters
     * into Domain Value Objects, executes the query via the repository, and maps the
     * resulting entities into resources suitable for external use.
     *
     * @param plotId The ID of the plot.
     * @param readingType The type of reading as a string.
     * @param start The start date/time.
     * @param end The end date/time.
     * @return A list of ReadingResource objects.
     */
    @Override
    public List<ReadingResource> fetchReadingsByPlotAndTypeAndPeriod(Long plotId, String readingType, LocalDateTime start, LocalDateTime end) {

        var pId = new PlotId(plotId);
        var rType = ReadingType.valueOf(readingType);

        var readings = readingRepository.findByPlotIdAndTypeAndMeasuredAtBetween(pId, rType, start, end);


        return readings.stream()
                .map(reading -> new ReadingResource(
                        reading.getReadingValue().getValue(),
                        reading.getReadingValue().getUnit(),
                        reading.getMeasuredAt()))
                .toList();
    }

}
