package com.service.powercrm.service;

import com.service.powercrm.dto.FipeResponse;
import com.service.powercrm.dto.YearDTO;
import com.service.powercrm.exception.ResourceNotFoundException;
import com.service.powercrm.service.integracao.FipeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FipeService {

    private final FipeClient fipeClient;

    public FipeResponse consultarPrecoFipe(String ano, Long marcaId, Long modeloId) {
        validateYearExists(ano, marcaId, modeloId);
        return fipeClient.getFipePrice(ano, marcaId, modeloId);
    }

    public void validateYearExists(String ano, Long marcaId, Long modeloId) {
        YearDTO[] anos = fipeClient.listAvailableYears(marcaId, modeloId);

        if (anos == null || Arrays.stream(anos).noneMatch(a -> a.getCode().equals(ano))) {
            throw new ResourceNotFoundException("Ano não disponível para o modelo informado.");
        }
    }
}
