package com.service.powercrm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class ModelResponse {

    @JsonProperty("modelos")
    private ModelDTO[] modelos;
}
