package com.service.powercrm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FipeResponse {

    @JsonProperty("TipoVeiculo")
    private Long vehicleType;

    @JsonProperty("Valor")
    private String price;

    @JsonProperty("Marca")
    private String brand;

    @JsonProperty("Modelo")
    private String model;

    @JsonProperty("AnoModelo")
    private String year;

    @JsonProperty("Combustivel")
    private String fuelType;

    @JsonProperty("MesReferencia")
    private String referenceMonth;

    @JsonProperty("SiglaCombustivel")
    private String fuelAbbreviation;

    @JsonProperty("nome")
    @Size(max = 200)
    private String name;
}
