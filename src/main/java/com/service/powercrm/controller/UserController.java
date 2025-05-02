package com.service.powercrm.controller;

import com.service.powercrm.dto.UpdateStatusUserDTO;
import com.service.powercrm.dto.UserDTO;
import com.service.powercrm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO dto) {
        UserDTO user = service.create(dto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<UserDTO> list(@ModelAttribute UserDTO filter, Pageable pageable) {
        return service.listAll(filter, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable Long id) {
        UserDTO user = service.findUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        UserDTO user = service.update(id, dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/period")
    public ResponseEntity<List<UserDTO>> findByPeriod(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime end) {
        List<UserDTO> user = service.findByPeriod(start, end);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    
    @PutMapping({"/{id}/status"})
    public ResponseEntity<UserDTO> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusUserDTO status) {
        UserDTO user = service.updateStatus(id, status);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
        
}
