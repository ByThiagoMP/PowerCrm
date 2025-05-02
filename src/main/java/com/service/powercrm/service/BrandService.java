package com.service.powercrm.service;

import org.springframework.stereotype.Service;

import com.service.powercrm.domain.Brand;
import com.service.powercrm.exception.ResourceNotFoundException;
import com.service.powercrm.mapper.BrandMapper;
import com.service.powercrm.repository.BrandRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper mapper;

        public Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrado com ID: " + id));
    }

}
