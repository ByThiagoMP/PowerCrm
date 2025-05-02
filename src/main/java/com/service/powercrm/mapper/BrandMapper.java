package com.service.powercrm.mapper;

import org.mapstruct.*;

import com.service.powercrm.domain.Brand;
import com.service.powercrm.dto.BrandDTO;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    Brand toEntity(BrandDTO dto);

    BrandDTO toDTO(Brand brand);
}
