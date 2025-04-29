package com.service.powercrm.service;

import com.service.powercrm.dto.VehicleDTO;
import com.service.powercrm.dto.VehicleWithUserDTO;
import com.service.powercrm.exception.ResourceAlreadyExistsException;
import com.service.powercrm.exception.ResourceNotFoundException;
import com.service.powercrm.mapper.VehicleMapper;
import com.service.powercrm.model.Vehicle;
import com.service.powercrm.repository.VehicleRepository;
import com.service.powercrm.model.User;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final UserService userService;

    @CacheEvict(value = { "vehicleCache", "vehicleListCache" }, allEntries = true)
    public VehicleDTO create(VehicleDTO dto) {
        validateVehiclesFields(dto);

        Vehicle vehicle = vehicleMapper.toEntity(dto);

        User user = userService.getUserById(dto.getUserId());

        vehicle.setUser(user);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toDTO(savedVehicle);
    }

    @Cacheable(value = "vehicleListCache", key = "{#filter.hashCode(), #pageable.pageNumber, #pageable.pageSize}")
    public Page<VehicleWithUserDTO> listAll(VehicleDTO filter, Pageable pageable) {
        // 1) Cria “exemplo” de Vehicle com os campos não-nulos do filtro
        Vehicle probe = Vehicle.builder()
                .plate(filter.getPlate())
                .year(filter.getYear())
                .advertisedPrice(filter.getAdvertisedPrice())
                .build();

        // 2) Matcher para “contém” e ignorar nulos
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        Example<Vehicle> example = Example.of(probe, matcher);

        // 3) Busca paginada e mapeia cada Vehicle → VehicleDTO
        return vehicleRepository.findAll(example, pageable)
                .map(vehicleMapper::toVehicleWithUserDTO);
    }

    @CacheEvict(value = "vehicleCache", key = "#id")
    public VehicleDTO update(Long id, VehicleDTO dto) {
        Vehicle vehicle = getVehicleById(id);

        vehicleMapper.updateFromDto(dto, vehicle);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toDTO(saved);
    }

    @Cacheable(value = "vehicleCache", key = "#id")
    public VehicleWithUserDTO findVehicleById(Long id) {
        Vehicle vehicle = getVehicleById(id);
        return vehicleMapper.toVehicleWithUserDTO(vehicle);
    }

    @CacheEvict(value = "vehicleCache", key = "#id")
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
    }

    public void validateVehiclesFields(VehicleDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (vehicleRepository.existsByPlate(dto.getPlate())) {
            errors.put("placa", "Já existe um veículo cadastrado com esta placa.");
        }

        if (!errors.isEmpty()) {
            throw new ResourceAlreadyExistsException(errors);
        }
    }
}
