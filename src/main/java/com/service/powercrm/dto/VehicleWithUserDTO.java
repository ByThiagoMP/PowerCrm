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
    private Integer year;
    private UserSummaryDTO user;
}
