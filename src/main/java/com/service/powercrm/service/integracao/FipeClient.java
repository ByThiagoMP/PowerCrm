package com.service.powercrm.service.integracao;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.service.powercrm.dto.BrandDTO;
import com.service.powercrm.dto.FipeResponse;
import com.service.powercrm.dto.ModelDTO;
import com.service.powercrm.dto.ModelResponse;
import com.service.powercrm.dto.YearDTO;

@Component
@RequiredArgsConstructor
public class FipeClient {

    // *Caso Execeda o Limite de Requisições da API FIPE terá que fazer o cadastro
    // em https://fipeapi.com.br/ e gerar um Token de Acesso.
    // configurar o Token no application.properties e descomentar a linha abaixo.*
    // @Value("${fipe.api.token}")
    // private String fipeApiToken;

    // private HttpEntity<Void> createHttpEntity() {
    // HttpHeaders headers = new HttpHeaders();
    // // headers.setBearerAuth(fipeApiToken);
    // return new HttpEntity<>(headers);
    // }

    private final RestTemplate restTemplate;

    @Cacheable(value = "fipeCache", key = "'marcas'")
    public BrandDTO[] listBrands() {
        String url = "https://parallelum.com.br/fipe/api/v1/carros/marcas";
        ResponseEntity<BrandDTO[]> response = restTemplate.exchange(url, HttpMethod.GET, null, BrandDTO[].class);
        return validateResponse(response, "Erro ao obter marcas FIPE");
    }

    @Cacheable(value = "fipeCache", key = "'modelos-' + #brandCode")
    public ModelDTO[] listModels(Long brandCode) {
        String url = String.format("https://parallelum.com.br/fipe/api/v1/carros/marcas/%d/modelos", brandCode);
        ResponseEntity<ModelResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, ModelResponse.class);

        ModelResponse body = validateResponse(response, "Erro ao obter modelos FIPE para marca " + brandCode);

        return body.getModelos();
    }

    @Cacheable(value = "fipeCache", key = "'preco-' + #yearCode + '-' + #brandCode + '-' + #modelCode")
    public FipeResponse getFipePrice(String yearCode, Long brandCode, Long modelCode) {
        String url = String.format(
                "https://parallelum.com.br/fipe/api/v1/carros/marcas/%d/modelos/%d/anos/%s",
                brandCode, modelCode, yearCode);

        return validateResponse(
                restTemplate.exchange(url, HttpMethod.GET, null, FipeResponse.class),
                String.format("Erro ao obter preço FIPE para marca %d, modelo %d, ano %s", brandCode, modelCode, yearCode)
        );
    }

    @Cacheable(value = "fipeCache", key = "'anos-' + #brandCode + '-' + #modelCode")
    public YearDTO[] listAvailableYears(Long brandCode, Long modelCode) {
        String url = String.format(
                "https://parallelum.com.br/fipe/api/v1/carros/marcas/%d/modelos/%d/anos/",
                brandCode, modelCode);

        return validateResponse(
                restTemplate.exchange(url, HttpMethod.GET, null, YearDTO[].class),
                String.format("Erro ao obter anos disponíveis para marca %d e modelo %d", brandCode, modelCode)
        );
    }

    private <T> T validateResponse(ResponseEntity<T> response, String errorMessage) {
        T body = response.getBody();
        if (response.getStatusCode() != HttpStatus.OK || body == null) {
            throw new HttpClientErrorException(
                    response.getStatusCode(),
                    errorMessage);
        }
        return body;
    }
}
