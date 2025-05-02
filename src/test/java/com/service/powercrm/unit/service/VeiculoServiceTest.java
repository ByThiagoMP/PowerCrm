package com.service.powercrm.unit.service;

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
import com.service.powercrm.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private VehicleMapper vehicleMapper;
    @Mock
    private UserService userService;
    @Mock
    private BrandService brandService;
    @Mock
    private ModelService modelService;
    @Mock
    private FipeService fipeService;
    @Mock
    private VehicleKafkaProducer vehicleKafkaProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createVehicle_shouldSaveAndReturnVehicleDTO() {
        VehicleDTO dto = new VehicleDTO();
        dto.setUserId(1L);
        dto.setModelId(2L);
        dto.setBrandId(3L);
        dto.setYear("2022");
        dto.setPlate("GWC3255");

        Vehicle vehicle = new Vehicle();
        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);

        User user = new User();
        Model model = new Model();
        model.setCode(002L);
        Brand brand = new Brand();
        brand.setCode(001L);

        when(vehicleRepository.existsByPlate("GWC3255")).thenReturn(false);
        when(vehicleMapper.toEntity(dto)).thenReturn(vehicle);
        when(userService.getUserById(1L)).thenReturn(user);
        when(modelService.getModelById(2L)).thenReturn(model);
        when(brandService.getBrandById(3L)).thenReturn(brand);
        when(vehicleRepository.save(vehicle)).thenReturn(savedVehicle);
        when(vehicleMapper.toDTO(savedVehicle)).thenReturn(dto);

        VehicleDTO result = vehicleService.create(dto);

        assertNotNull(result);
        verify(vehicleKafkaProducer).sendValidationEvent(any(VehicleValidationEvent.class));
    }

    @Test
    void createVehicle_shouldThrowExceptionWhenPlateExists() {
        VehicleDTO dto = new VehicleDTO();
        dto.setPlate("DUPLICADA");

        when(vehicleRepository.existsByPlate("DUPLICADA")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> vehicleService.create(dto));
    }

    @Test
    void listAll_shouldReturnPageOfVehicles() {
        VehicleDTO filter = new VehicleDTO();
        filter.setPlate("TEST");

        Vehicle vehicle = new Vehicle();
        VehicleWithUserDTO dto = new VehicleWithUserDTO();
        Pageable pageable = PageRequest.of(0, 10);

        when(vehicleRepository.findAll(any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(vehicle)));
        when(vehicleMapper.toVehicleWithUserDTO(vehicle)).thenReturn(dto);

        var result = vehicleService.listAll(filter, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateVehicle_shouldSaveAndReturnUpdatedVehicleDTO() {
        VehicleDTO dto = new VehicleDTO();
        dto.setUserId(1L);
        dto.setModelId(2L);
        dto.setBrandId(3L);
        dto.setYear("2023");
        dto.setPlate("XYZ5678");

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setId(1L);

        User user = new User();
        Model model = new Model();
        model.setCode(003l);
        Brand brand = new Brand();
        brand.setCode(004l);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toEntity(dto)).thenReturn(vehicle);
        when(userService.getUserById(1L)).thenReturn(user);
        when(modelService.getModelById(2L)).thenReturn(model);
        when(brandService.getBrandById(3L)).thenReturn(brand);
        when(vehicleRepository.save(vehicle)).thenReturn(updatedVehicle);
        when(vehicleMapper.toDTO(updatedVehicle)).thenReturn(dto);

        VehicleDTO result = vehicleService.update(1L, dto);

        assertNotNull(result);
        assertEquals("XYZ5678", result.getPlate());
    }

    @Test
    void updateVehicle_shouldThrowExceptionWhenVehicleNotFound() {
        VehicleDTO dto = new VehicleDTO();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.update(1L, dto));
    }

    @Test
    void findVehicleById_shouldReturnVehicleWithUserDTO() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        VehicleWithUserDTO dto = new VehicleWithUserDTO();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toVehicleWithUserDTO(vehicle)).thenReturn(dto);

        VehicleWithUserDTO result = vehicleService.findVehicleById(1L);

        assertNotNull(result);
    }

    @Test
    void findVehicleById_shouldThrowExceptionWhenVehicleNotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.findVehicleById(1L));
    }

    @Test
    void deleteVehicle_shouldDeleteVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(new Vehicle()));

        vehicleService.delete(1L);

        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty()); // Mocka o retorno do findById

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.delete(1L); // Chama o método delete, que deve lançar a exceção
        });

        // Verifica se a exceção contém a mensagem esperada
        assertEquals("Veículo não encontrado", exception.getMessage());

        // Verifica que o repositório não tentou deletar o usuário
        verify(vehicleRepository, never()).deleteById(1L);
    }
}
