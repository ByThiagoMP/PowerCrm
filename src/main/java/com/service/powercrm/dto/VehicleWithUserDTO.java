package com.service.powercrm.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleWithUserDTO {
    private Long id;
    private String plate;
    private BigDecimal advertisedPrice;
    private String year;
    private UserSummaryDTO user;
    private BrandDTO brand;
    private ModelDTO model;
    private BigDecimal fipePrice;
}
