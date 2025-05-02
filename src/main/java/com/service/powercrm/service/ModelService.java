package com.service.powercrm.service;

import org.springframework.stereotype.Service;

import com.service.powercrm.domain.Model;
import com.service.powercrm.exception.ResourceNotFoundException;
import com.service.powercrm.mapper.ModelMapper;
import com.service.powercrm.repository.ModelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelService {
    private final ModelRepository modelRepository;
    private final ModelMapper mapper;

        public Model getModelById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo não encontrado com ID: " + id));
    }

}
