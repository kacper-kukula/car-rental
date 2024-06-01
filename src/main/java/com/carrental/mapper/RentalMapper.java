package com.carrental.mapper;

import com.carrental.config.MapperConfig;
import com.carrental.dto.rental.RentalResponseDto;
import com.carrental.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "carId", source = "car.id")
    RentalResponseDto toDto(Rental rental);
}
