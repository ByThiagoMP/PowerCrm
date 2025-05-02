package com.service.powercrm.mapper;

import org.mapstruct.*;

import com.service.powercrm.domain.User;
import com.service.powercrm.dto.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? dto.getStatus() : \"Ativo\")")
    User toModel(UserDTO dto);

    UserDTO toDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateFromDto(UserDTO dto, @MappingTarget User entity);
}
