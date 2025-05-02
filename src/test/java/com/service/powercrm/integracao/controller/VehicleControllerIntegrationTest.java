package com.service.powercrm.integracao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.powercrm.config.KafkaMockConfig;
import com.service.powercrm.domain.Brand;
import com.service.powercrm.domain.Model;
import com.service.powercrm.domain.User;
import com.service.powercrm.dto.FipeResponse;
import com.service.powercrm.dto.VehicleDTO;
import com.service.powercrm.repository.BrandRepository;
import com.service.powercrm.repository.ModelRepository;
import com.service.powercrm.repository.UserRepository;
import com.service.powercrm.repository.VehicleRepository;
import com.service.powercrm.service.FipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
@Import({
                VehicleControllerIntegrationTest.StubFipeConfig.class,
                com.service.powercrm.config.KafkaMockConfig.class
})
public class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelRepository modelRepository;

    private User user;
    private Brand brand;
    private Model model;

    @TestConfiguration
    static class StubFipeConfig {
        @Bean
        public FipeService fipeService() {
            return new FipeService(null) {
                @Override
                public FipeResponse consultarPrecoFipe(String ano, Long marcaId, Long modeloId) {
                    return FipeResponse.builder()
                            .price("R$ 48000.00")
                            .build();
                }

                @Override
                public void validateYearExists(String ano, Long marcaId, Long modeloId) {
                    // No-op: sempre permite o ano
                }
            };
        }
    }

    @BeforeEach
    void setup() {
        vehicleRepository.deleteAll();
        modelRepository.deleteAll();
        brandRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder()
                .name("Teste User")
                .email("user@test.com")
                .cpf("12345678909")
                .status("ACTIVE")
                .build());

        brand = brandRepository.save(Brand.builder()
                .name("Marca Teste")
                .build());

        model = modelRepository.save(Model.builder()
                .name("Modelo Teste")
                .brand(brand)
                .build());
    }

    @Test
    void givenValidVehicle_whenCreate_thenReturnsCreated() throws Exception {
        VehicleDTO dto = VehicleDTO.builder()
                .plate("ABC1234")
                .advertisedPrice(new BigDecimal("50000.00"))
                .year("1992-1")
                .userId(user.getId())
                .brandId(brand.getId())
                .modelId(model.getId())
                .fipePrice(new BigDecimal("48000.00"))
                .build();

        mockMvc.perform(post("/api/vehicle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.plate").value("ABC1234"))
            .andExpect(jsonPath("$.fipePrice").value(48000.00));

        assertThat(vehicleRepository.findAll()).hasSize(1);
    }

    @Test
    void givenVehicle_whenUpdate_thenReturnsUpdated() throws Exception {
        var saved = vehicleRepository.save(com.service.powercrm.domain.Vehicle.builder()
                .plate("OLD1234")
                .advertisedPrice(new BigDecimal("30000.00"))
                .year("1992-1")
                .user(user)
                .brand(brand)
                .model(model)
                .fipePrice(new BigDecimal("48000.00"))
                .build());

        VehicleDTO updateDto = VehicleDTO.builder()
                .plate("NEW5678")
                .advertisedPrice(new BigDecimal("35000.00"))
                .year("1992-1")
                .userId(user.getId())
                .brandId(brand.getId())
                        .modelId(model.getId())
                        .fipePrice(new BigDecimal("48000.00"))
                .build();

        mockMvc.perform(put("/api/vehicle/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.plate").value("NEW5678"))
            .andExpect(jsonPath("$.fipePrice").value(48000.00));
    }

    @Test
    void givenVehicle_whenFindById_thenReturnsVehicle() throws Exception {
        var saved = vehicleRepository.save(com.service.powercrm.domain.Vehicle.builder()
                .plate("XYZ9999")
                .advertisedPrice(new BigDecimal("40000.00"))
                .year("1992-1")
                .user(user)
                .brand(brand)
                .model(model)
                .fipePrice(new BigDecimal("42000.00"))
                .build());

        mockMvc.perform(get("/api/vehicle/{id}", saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.plate").value("XYZ9999"))
            .andExpect(jsonPath("$.fipePrice").value(42000.00))
            .andExpect(jsonPath("$.user.id").value(user.getId()));
    }

    @Test
    void givenVehicle_whenDelete_thenReturnsOk() throws Exception {
        var saved = vehicleRepository.save(com.service.powercrm.domain.Vehicle.builder()
                .plate("DEL1234")
                .advertisedPrice(new BigDecimal("45000.00"))
                .year("1992-1")
                .user(user)
                .brand(brand)
                .model(model)
                .fipePrice(new BigDecimal("45000.00"))
                .build());

        mockMvc.perform(delete("/api/vehicle/{id}", saved.getId()))
            .andExpect(status().isOk());

        assertThat(vehicleRepository.findById(saved.getId())).isEmpty();
    }
}
