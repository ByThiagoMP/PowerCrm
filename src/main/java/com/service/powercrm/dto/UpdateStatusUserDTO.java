package com.service.powercrm.dto;

import jakarta.validation.constraints.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusUserDTO {

    @NotBlank
    private String status;
    
}
