package com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record OrganizationId(Long organizationId) {
    public OrganizationId() {
        this(null);
    }

    public OrganizationId {
        if (organizationId == null || organizationId < 1)
            throw new IllegalArgumentException("organizationId must be greater than zero");
    }
}

