package com.service.powercrm.unit.service;

import com.service.powercrm.domain.User;
import com.service.powercrm.dto.UserDTO;
import com.service.powercrm.exception.ResourceAlreadyExistsException;
import com.service.powercrm.exception.ResourceNotFoundException;
import com.service.powercrm.mapper.UserMapper;
import com.service.powercrm.repository.UserRepository;
import com.service.powercrm.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("João")
                .email("joao@email.com")
                .cpf("52795650088")
                .createdAt(LocalDateTime.now())
                .build();

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("João");
        userDTO.setEmail("joao@email.com");
        userDTO.setCpf("52795650088");
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(userDTO.getCpf())).thenReturn(false);
        when(userMapper.toModel(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.create(userDTO);

        assertNotNull(result);
        assertEquals("João", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deveLancarExcecaoSeEmailOuCpfJaExistir() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);
        when(userRepository.existsByCpf(userDTO.getCpf())).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.create(userDTO);
        });

        assertTrue(ex.getErrors().containsKey("email"));
        assertTrue(ex.getErrors().containsKey("cpf"));
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateFromDto(userDTO, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO updated = userService.update(1L, userDTO);

        assertEquals("João", updated.getName());
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserById(1L);
        });
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.findUserById(1L);

        assertEquals("João", result.getName());
    }

    @Test
    void deveBuscarUsuariosPorPeriodo() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        when(userRepository.findAllByCreatedAtBetween(start, end)).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.findByPeriod(start, end);

        assertEquals(1, result.size());
        assertEquals("João", result.get(0).getName());
    }

    @Test
    void deveLancarExcecaoSePeriodoInvalido() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.findByPeriod(start, end);
        });

        assertEquals("A data de início não pode ser posterior à data de fim.", ex.getMessage());
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        // Mocka o comportamento do findById para retornar um usuário
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        // Chama o método delete
        userService.delete(1L);

        // Verifica se o delete foi chamado exatamente uma vez
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // Mocka o comportamento do findById para retornar um Optional.empty (usuário
        // inexistente)
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica se a exceção é lançada quando o usuário não é encontrado
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.delete(1L);
        });

        // Verifica a mensagem da exceção
        assertEquals("Usuário não encontrado com ID: 1", exception.getMessage());

        // Verifica que o método deleteById não foi chamado
        verify(userRepository, never()).deleteById(1L);
    }
}
