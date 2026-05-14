package com.floweytech.agrotrack.monitoringservice.interfaces.acl;

import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.ReadingResource;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Monitoring Context Facade Interface
 * @summary
 * Defines the contract for the Anti-Corruption Layer (ACL) of the Monitoring & Control Context.
 * It exposes specific monitoring data and capabilities to other Bounded Contexts (like Reports)
 * ensuring loose coupling by returning Resources (DTOs) instead of Domain Entities.
 *
 * @author FloweyTech developer team
 */
public interface MonitoringContextFacade {
    /**
     * Fetch Readings by Plot, Type, and Period
     * @summary
     * Retrieves a list of environmental readings for a specific plot, reading type,
     * and time range. This method is primarily used by external contexts to obtain
     * raw data for processing or reporting.
     *
     * @param plotId The unique identifier of the plot.
     * @param readingType The type of reading to fetch (e.g., "TEMPERATURE", "HUMIDITY").
     * @param start The start date and time of the period.
     * @param end The end date and time of the period.
     * @return A list of {@link ReadingResource} containing the filtered reading data.
     */
    List<ReadingResource> fetchReadingsByPlotAndTypeAndPeriod(
            Long plotId,
            String readingType,
            LocalDateTime start,
            LocalDateTime end
    );
}
