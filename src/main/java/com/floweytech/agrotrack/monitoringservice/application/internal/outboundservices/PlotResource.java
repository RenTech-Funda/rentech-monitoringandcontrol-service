package com.floweytech.agrotrack.monitoringservice.application.internal.outboundservices;

public record PlotResource(
        Long id,
        Long plotId,
        String plotName,
        Double size,
        String unit,
        Long plantTypeId,
        String location,
        Long organizationId
) {
}
