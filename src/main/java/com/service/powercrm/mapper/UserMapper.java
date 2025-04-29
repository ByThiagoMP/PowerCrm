package com.service.powercrm.mapper;

import org.mapstruct.*;
import com.service.powercrm.dto.UserDTO;
import com.service.powercrm.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? dto.getStatus() : \"Ativo\")")
    User toModel(UserDTO dto);

    UserDTO toDTO(User user);

    // // Se precisar customizar após mapeamento:
    // @AfterMapping
    // default void normalizePhone(@MappingTarget User user) {
    //     if (user.getPhone() != null) {
    //         // remove tudo que não for dígito
    //         user.setPhone(user.getPhone().replaceAll("\\D+", ""));
    //     }
    // }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateFromDto(UserDTO dto, @MappingTarget User entity);
}
