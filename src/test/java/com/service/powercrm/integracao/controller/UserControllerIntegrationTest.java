package com.service.powercrm.integracao.controller;

import com.service.powercrm.dto.UserDTO;
import com.service.powercrm.config.KafkaMockConfig;
import com.service.powercrm.domain.User;
import com.service.powercrm.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import({VehicleControllerIntegrationTest.StubFipeConfig.class, KafkaMockConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void givenValidUser_whenCreate_thenReturnsCreated() throws Exception {
        UserDTO dto = UserDTO.builder()
                .name("João Silva")
                .email("joao.silva@example.com")
                .cpf("947.710.610-09")
                .phone("11999998888")
                .zipCode("01001000")
                .address("Rua A")
                .addressNumber("100")
                .status("Ativo")    
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("João Silva"));

        List<User> all = userRepository.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    void givenUsers_whenList_thenReturnsPage() throws Exception {
        // prepara dados
        for (int i = 1; i <= 5; i++) {
            User u = User.builder()
                    .name("User " + i)
                    .email("user" + i + "@example.com")
                    .cpf("1234567890" + i)
                    .status("Ativo")
                    .build();
            userRepository.save(u);
        }

        mockMvc.perform(get("/api/users?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    void givenUser_whenFindById_thenReturnsUser() throws Exception {
        User saved = userRepository.save(
                User.builder().name("Ana").email("ana@example.com").cpf("98765432100").status("Ativo").build());

        mockMvc.perform(get("/api/users/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value(saved.getName()))
                .andExpect(jsonPath("$.email").value(saved.getEmail()))
                .andExpect(jsonPath("$.cpf").value(saved.getCpf()))
                .andExpect(jsonPath("$.status").value(saved.getStatus()));
    }

    @Test
    void givenUser_whenUpdate_thenReturnsUpdated() throws Exception {
        User saved = userRepository.save(
                User.builder()
                        .name("Carlos")
                        .email("carlos@example.com")
                        .cpf("947.710.610-09")
                        .status("Ativo")
                        .build());

        UserDTO updateDto = UserDTO.builder()
                .name("Carlos Modified")
                .email("carlos.mod@example.com")
                .cpf("947.710.610-09")
                .phone("11988887777")
                .zipCode("02002000")
                .address("Rua B")
                .addressNumber("200")
                .complement("Apt 2")
                .status("Ativo")
                .build();

        mockMvc.perform(put("/api/users/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos Modified"));
    }

    @Test
    void givenUser_whenDelete_thenNoContent() throws Exception {
        User saved = userRepository.save(
                User.builder().name("Mariana").email("mariana@example.com").cpf("55566677788").status("Ativo").build());

        mockMvc.perform(delete("/api/users/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void givenUsersInPeriod_whenFindByPeriod_thenReturnsList() throws Exception {
            // define a window around now so @CreationTimestamp values fall within
            LocalDateTime start = LocalDateTime.now().minusMinutes(1);
            LocalDateTime end = LocalDateTime.now().plusMinutes(1);

            User uOld = User.builder()
                            .name("Old User")
                            .email("old@example.com")
                            .cpf("00011122233")
                            .status("Ativo")
                            .build();
            User uRecent = User.builder()
                            .name("Recent User")
                            .email("recent@example.com")
                            .cpf("44455566677")
                            .status("Ativo")
                            .build();
            userRepository.saveAll(List.of(uOld, uRecent));

            mockMvc.perform(get("/api/users/period")
                            .param("start", start.format(FORMATTER))
                            .param("end", end.format(FORMATTER)))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.length()").value(2))
                            .andExpect(jsonPath("$[?(@.email=='old@example.com')]").exists())
                            .andExpect(jsonPath("$[?(@.email=='recent@example.com')]").exists());
    }
    
        @Test
        void givenUser_whenUpdateStatus_thenReturnsUpdatedStatus() throws Exception {
                User saved = userRepository.save(
                                User.builder()
                                                .name("Carlos")
                                                .email("carlos@example.com")
                                                .cpf("947.710.610-09")
                                                .status("Ativo")
                                                .build());

                UserDTO updateDto = UserDTO.builder()
                                .name("Carlos")
                                .email("carlos.mod@example.com")
                                .cpf("947.710.610-09")
                                .phone("11988887777")
                                .zipCode("02002000")
                                .address("Rua B")
                                .addressNumber("200")
                                .complement("Apt 2")
                                .status("Desativado")
                                .build();

                mockMvc.perform(put("/api/users/{id}", saved.getId(), "Desativado")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("Desativado"));
        }
}
