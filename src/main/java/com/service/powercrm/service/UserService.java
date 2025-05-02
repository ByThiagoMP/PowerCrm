package com.service.powercrm.service;

import com.service.powercrm.domain.User;
import com.service.powercrm.dto.UpdateStatusUserDTO;
import com.service.powercrm.dto.UserDTO;
import com.service.powercrm.exception.ResourceAlreadyExistsException;
import com.service.powercrm.exception.ResourceNotFoundException;
import com.service.powercrm.mapper.UserMapper;
import com.service.powercrm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserDTO create(UserDTO dto) {
        validateUserFields(dto);

        User user = mapper.toModel(dto);
        User saved = userRepository.save(user);
        return mapper.toDTO(saved);
    }

    public Page<UserDTO> listAll(UserDTO filter, Pageable pageable) {
        User probe = User.builder()
                .name(filter.getName())
                .email(filter.getEmail())
                .status(filter.getStatus())
                .build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        Example<User> example = Example.of(probe, matcher);

        return userRepository.findAll(example, pageable)
                .map(mapper::toDTO);
    }

    public UserDTO update(Long id, UserDTO dto) {
        User user = getUserById(id);

        mapper.updateFromDto(dto, user);
        User saved = userRepository.save(user);
        return mapper.toDTO(saved);
    }

    public void delete(Long id) {
        getUserById(id);
        userRepository.deleteById(id);
    }

    public UserDTO findUserById(Long id) {
        User user = getUserById(id);

        return mapper.toDTO(user);
    }

    public List<UserDTO> findByPeriod(LocalDateTime start, LocalDateTime end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("As datas de início e fim são obrigatórias.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");
        }

        List<User> users = userRepository.findAllByCreatedAtBetween(start, end);

        return users.stream()
                .map(mapper::toDTO)
                .toList();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    public void validateUserFields(UserDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (userRepository.existsByEmail(dto.getEmail())) {
            errors.put("email", "E-mail já cadastrado");
        }

        if (userRepository.existsByCpf(dto.getCpf())) {
            errors.put("cpf", "CPF já cadastrado");
        }

        if (!errors.isEmpty()) {
            throw new ResourceAlreadyExistsException(errors);
        }
    }

    public UserDTO updateStatus(Long id, UpdateStatusUserDTO updateStatus) {
        User user = getUserById(id);
        user.setStatus(updateStatus.getStatus());
        User saved = userRepository.save(user);
        return mapper.toDTO(saved);
    }
}