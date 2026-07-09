package com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

public record TaskResource(
        Long id,
        Long createdByUserId,
        Long assignedToUserId,
        Long organizationId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String taskStatus,
        List<MaterialUsedResource> materialsUsed
) {
}
