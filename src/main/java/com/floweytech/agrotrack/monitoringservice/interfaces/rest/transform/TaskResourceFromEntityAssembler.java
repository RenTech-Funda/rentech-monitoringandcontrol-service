package com.floweytech.agrotrack.monitoringservice.interfaces.rest.transform;

import com.floweytech.agrotrack.monitoringservice.domain.model.aggregates.Task;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.MaterialUsedResource;
import com.floweytech.agrotrack.monitoringservice.interfaces.rest.resources.TaskResource;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.stream.Collectors;

public class TaskResourceFromEntityAssembler {

    /**
     * Converts a Task entity to a TaskResource with translations.
     * @param entity Task entity to convert
     * @param messageSource MessageSource for translations
     * @return TaskResource created from the entity with translated values
     */
    public static TaskResource toResourceFromEntity(Task entity, MessageSource messageSource) {
        // Convert MaterialUsed (domain) to MaterialUsedResource (REST) with translations
        var materialsUsedResources = entity.getMaterialsUsed().stream()
                .map(material -> {
                    String translatedUnit = messageSource.getMessage(
                            "material.unit." + material.getUnit(),
                            null,
                            material.getUnit(), // fallback to original value
                            LocaleContextHolder.getLocale()
                    );
                    return new MaterialUsedResource(
                            material.getMaterialName(),
                            material.getQuantity(),
                            translatedUnit
                    );
                })
                .collect(Collectors.toList());

        return new TaskResource(
                entity.getId(),
                entity.getAssigneeProfileId().profileId(),
                entity.getAssignedToProfileId().profileId(),
                entity.getOrganizationId().organizationId(),
                entity.getTaskDetails().getTaskTitle(),
                entity.getTaskDetails().getTaskDescription(),
                entity.getDateRange().getStartDate(),
                entity.getDateRange().getEndDate(),
                entity.getTaskStatus().name(),
                materialsUsedResources
        );
    }
}
