package com.service.powercrm.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Year;

import com.service.powercrm.model.User;

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

    @NotNull(message = "{vehicle.year.required}")
    @Min(value = 1980, message = "vehicle.year.min")
    @Max(value = 2999, message = "vehicle.year.max")
    private Integer year;

    @NotNull(message = "vehicle.userId.required")
    private Long userId;
}
