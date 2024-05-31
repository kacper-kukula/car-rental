package com.carrental.mapper;

import com.carrental.config.MapperConfig;
import com.carrental.dto.rental.RentalCreateRequestDto;
import com.carrental.dto.rental.RentalResponseDto;
import com.carrental.model.Rental;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {

    RentalResponseDto toDto(Rental rental);

    Rental toEntity(RentalCreateRequestDto requestDto);
}
