package com.service.powercrm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class YearResponse {

    @JsonProperty("anos")
    private List<YearDTO> anos;
}
