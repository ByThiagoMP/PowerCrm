package com.service.powercrm.service;

import com.service.powercrm.domain.Brand;
import com.service.powercrm.domain.Model;
import com.service.powercrm.domain.User;
import com.service.powercrm.domain.Vehicle;
import com.service.powercrm.dto.VehicleDTO;
import com.service.powercrm.dto.VehicleValidationEvent;
import com.service.powercrm.dto.VehicleWithUserDTO;
import com.service.powercrm.exception.ResourceAlreadyExistsException;
import com.service.powercrm.exception.ResourceNotFoundException;
import com.service.powercrm.kafka.VehicleKafkaProducer;
import com.service.powercrm.mapper.VehicleMapper;
import com.service.powercrm.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final UserService userService;
    private final BrandService brandService;
    private final ModelService modelService;
    private final FipeService fipeService;
    private final VehicleKafkaProducer vehicleKafkaProducer;

    @Transactional
    @CacheEvict(value = { "vehicleCache", "vehicleListCache" }, allEntries = true)
    public VehicleDTO create(VehicleDTO dto) {
        validateVehiclesFields(dto);

        Vehicle vehicle = vehicleMapper.toEntity(dto);

        User user = userService.getUserById(dto.getUserId());
        Model model = modelService.getModelById(dto.getModelId());
        Brand brand = brandService.getBrandById(dto.getBrandId());

        fipeService.validateYearExists(dto.getYear(), brand.getCode(), model.getCode());

        vehicle.setModel(model);
        vehicle.setBrand(brand);
        vehicle.setUser(user);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        VehicleValidationEvent event = new VehicleValidationEvent(
                savedVehicle.getId(),
                brand.getCode(),
                model.getCode(),
                dto.getYear());
        vehicleKafkaProducer.sendValidationEvent(event);

        return vehicleMapper.toDTO(savedVehicle);
    }

    @Cacheable(value = "vehicleListCache", key = "{#filter.hashCode(), #pageable.pageNumber, #pageable.pageSize}")
    public Page<VehicleWithUserDTO> listAll(VehicleDTO filter, Pageable pageable) {

        Vehicle probe = Vehicle.builder()
                .plate(filter.getPlate())
                .advertisedPrice(filter.getAdvertisedPrice())
                .build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        Example<Vehicle> example = Example.of(probe, matcher);

        return vehicleRepository.findAll(example, pageable)
                .map(vehicleMapper::toVehicleWithUserDTO);
    }

    @Transactional
    @CacheEvict(value = { "vehicleCache", "vehicleListCache" }, allEntries = true)
    public VehicleDTO update(Long id, VehicleDTO dto) {
        validateVehiclesFields(dto);

        Vehicle vehicle = getVehicleById(id);

        User user = userService.getUserById(dto.getUserId());
        Model model = modelService.getModelById(dto.getModelId());
        Brand brand = brandService.getBrandById(dto.getBrandId());

        fipeService.validateYearExists(dto.getYear(), brand.getCode(), model.getCode());

        vehicle.setModel(model);
        vehicle.setBrand(brand);
        vehicle.setUser(user);

        vehicleMapper.updateFromDto(dto, vehicle);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        VehicleValidationEvent event = new VehicleValidationEvent(
                savedVehicle.getId(),
                brand.getCode(),
                model.getCode(),
                dto.getYear());
        vehicleKafkaProducer.sendValidationEvent(event);

        return vehicleMapper.toDTO(savedVehicle);
    }

    @Cacheable(value = "vehicleCache", key = "#id")
    public VehicleWithUserDTO findVehicleById(Long id) {
        Vehicle vehicle = getVehicleById(id);
        return vehicleMapper.toVehicleWithUserDTO(vehicle);
    }

    @CacheEvict(value = "vehicleCache", key = "#id")
    public void delete(Long id) {
        getVehicleById(id);
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
