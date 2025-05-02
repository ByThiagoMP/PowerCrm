package com.service.powercrm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDTO {

    private Long id;

    @JsonProperty("codigo")
    private Long code;

    @JsonProperty("nome")
    @NotBlank(message = "{user.name.required}")
    @Size(max = 200)
    private String name;

}
