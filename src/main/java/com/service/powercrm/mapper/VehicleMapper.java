package com.service.powercrm.mapper;

import org.mapstruct.*;

import com.service.powercrm.domain.Vehicle;
import com.service.powercrm.dto.VehicleDTO;
import com.service.powercrm.dto.VehicleWithUserDTO;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "user", ignore = true)
    Vehicle toEntity(VehicleDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "model.id", target = "modelId")
    @Mapping(source = "brand.id", target = "brandId")
    VehicleDTO toDTO(Vehicle vehicle);

    void updateFromDto(VehicleDTO dto, @MappingTarget Vehicle entity);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "model", target = "model")
    @Mapping(source = "brand", target = "brand")
    VehicleWithUserDTO toVehicleWithUserDTO(Vehicle vehicle);
}
