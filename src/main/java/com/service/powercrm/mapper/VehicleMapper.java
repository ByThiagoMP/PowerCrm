package com.service.powercrm.mapper;

import org.mapstruct.*;

import com.service.powercrm.dto.UserSummaryDTO;
import com.service.powercrm.dto.VehicleDTO;
import com.service.powercrm.dto.VehicleWithUserDTO;
import com.service.powercrm.model.User;
import com.service.powercrm.model.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    // Mapear DTO para Entity (sem user)
    @Mapping(target = "user", ignore = true) // User vai ser setado manualmente no service
    Vehicle toEntity(VehicleDTO dto);

    // Mapear Entity para DTO (útil para retorno)
    @Mapping(source = "user.id", target = "userId")
    VehicleDTO toDTO(Vehicle vehicle);
    // Se precisar customizar após mapeamento:
    // @AfterMapping
    // default void normalizePhone(@MappingTarget Vehicle vehicle) {
    // if (vehicle.getPhone() != null) {
    // // remove tudo que não for dígito
    // vehicle.setPhone(vehicle.getPhone().replaceAll("\\D+", ""));
    // }
    // }

    // @BeanMapping(nullValuePropertyMappingStrategy =
    // NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "id", ignore = true)
    // void updateFromDto(VehicleDTO dto, @MappingTarget Vehicle entity);
    // void updateFromDto(VehicleDTO dto, @MappingTarget Vehicle entity) {
    // if (dto.getId() != null) {
    // entity.setId(dto.getId());
    // }
    void updateFromDto(VehicleDTO dto, @MappingTarget Vehicle entity);

    @Mapping(source = "user", target = "user")
    VehicleWithUserDTO toVehicleWithUserDTO(Vehicle vehicle);
}
