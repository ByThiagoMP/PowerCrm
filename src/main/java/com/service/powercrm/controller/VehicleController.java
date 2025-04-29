package com.service.powercrm.controller;

import com.service.powercrm.dto.VehicleDTO;
import com.service.powercrm.dto.VehicleWithUserDTO;
import com.service.powercrm.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<VehicleDTO> create(@Valid @RequestBody VehicleDTO dto) {
        VehicleDTO vehicle = service.create(dto);
        return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<VehicleWithUserDTO> list(@ModelAttribute VehicleDTO filter, Pageable pageable) {
        return service.listAll(filter, pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(@PathVariable Long id, @Valid @RequestBody VehicleDTO dto) {
        VehicleDTO vehicle = service.update(id, dto);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleWithUserDTO> findVehicleById(@PathVariable Long id) {
        VehicleWithUserDTO vehicle = service.findVehicleById(id);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }
}
