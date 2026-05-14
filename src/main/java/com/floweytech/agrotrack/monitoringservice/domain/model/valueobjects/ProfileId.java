package com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record ProfileId(Long profileId) {
    public ProfileId() {
        this(null);
    }

    public ProfileId {
        if (profileId == null || profileId < 1)
            throw new IllegalArgumentException("profileId must be greater than zero");
    }
}
