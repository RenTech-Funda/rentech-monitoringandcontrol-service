package com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(Long userId) {
    public UserId() {
        this(null);
    }

    public UserId {
        if (userId == null || userId < 1)
            throw new IllegalArgumentException("userId must be greater than zero");
    }
}
