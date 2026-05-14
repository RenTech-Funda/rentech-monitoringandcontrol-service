package com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.LocalDate;

@Embeddable
@Getter
public class DateRange {
    private LocalDate startDate;
    private LocalDate endDate;

    protected DateRange() {
    }

    public DateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null)
            throw new IllegalArgumentException("startDate cannot be null");
        if (endDate == null)
            throw new IllegalArgumentException("endDate cannot be null");
        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException("endDate cannot be before startDate");
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
