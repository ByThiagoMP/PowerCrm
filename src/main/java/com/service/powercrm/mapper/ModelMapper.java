package com.service.powercrm.mapper;

import org.mapstruct.*;

import com.service.powercrm.domain.Model;
import com.service.powercrm.dto.ModelDTO;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    @Mapping(target = "brand", ignore = true)
    Model toEntity(ModelDTO dto);

    ModelDTO toDTO(Model model);
}
