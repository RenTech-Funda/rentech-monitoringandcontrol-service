package com.floweytech.agrotrack.monitoringservice.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class MaterialUsed {
    private String materialName;
    private Double quantity;
    private String unit;

    protected MaterialUsed() {
    }

    public MaterialUsed(String materialName, Double quantity, String unit) {
        if (materialName == null || materialName.isBlank())
            throw new IllegalArgumentException("materialName cannot be null or blank");
        if (quantity == null || quantity <= 0)
            throw new IllegalArgumentException("quantity must be greater than zero");
        if (unit == null || unit.isBlank())
            throw new IllegalArgumentException("unit cannot be null or blank");
        this.materialName = materialName;
        this.quantity = quantity;
        this.unit = unit;
    }
}
