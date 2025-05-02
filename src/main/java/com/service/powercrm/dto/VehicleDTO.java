package com.service.powercrm.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {

    private Long id;

    @NotNull(message = "{vehicle.plate.required}")
    @Size(max = 7, message = "{vehicle.plate.size}")
    // @Pattern(regexp = "^[A-Z]{3}-[0-9]{4}$", message = "vehicle.plate.invalid")
    private String plate;

    @NotNull(message = "{vehicle.advertisedPrice.required}")
    @DecimalMin(value = "0.00", message = "{vehicle.advertisedPrice.min}")
    @Digits(integer = 10, fraction = 2, message = "{vehicle.advertisedPrice.size}")
    private BigDecimal advertisedPrice;

    @NotBlank(message = "{vehicle.year.required}")
    @Size(min = 6, max = 7, message = "{vehicle.year.size}")
    @Pattern(regexp = "^[0-9]{4}-(?:[1-9]|1[0-2])$", message = "{vehicle.year.format}")
    @Column(nullable = false, name = "year", length = 7)
    private String year;

    @NotNull(message = "vehicle.userId.required")
    private Long userId;

    @NotNull(message = "vehicle.brandId.required")
    private Long brandId;

    @NotNull(message = "vehicle.modelId.required")
    private Long modelId;
    
    private BigDecimal fipePrice;
}
