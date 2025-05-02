package com.service.powercrm.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleValidationEvent {

    private Long vehicleId;
    private Long brandId;
    private Long modelId;
    private String year;
    
}
