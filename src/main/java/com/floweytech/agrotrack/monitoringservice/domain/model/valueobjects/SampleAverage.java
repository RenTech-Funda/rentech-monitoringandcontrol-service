package com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * Immutable value object representing the average values of a sampling session.
 */
@Embeddable
@Getter
public class SampleAverage {

    private Double avgHeightCm;
    private Double avgLeafCount;
    private Double avgFruitCount;

    protected SampleAverage() {
        // for JPA
    }

    public SampleAverage(Double avgHeightCm, Double avgLeafCount, Double avgFruitCount) {
        if (avgHeightCm == null || avgHeightCm < 0)
            throw new IllegalArgumentException("avgHeightCm cannot be negative");
        if (avgLeafCount == null || avgLeafCount < 0)
            throw new IllegalArgumentException("avgLeafCount cannot be negative");
        if (avgFruitCount == null || avgFruitCount < 0)
            throw new IllegalArgumentException("avgFruitCount cannot be negative");

        this.avgHeightCm = avgHeightCm;
        this.avgLeafCount = avgLeafCount;
        this.avgFruitCount = avgFruitCount;
    }

    public static SampleAverage empty() {
        return new SampleAverage(0.0, 0.0, 0.0);
    }
}
