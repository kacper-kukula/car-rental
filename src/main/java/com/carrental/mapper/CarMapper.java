package com.carrental.mapper;

import com.carrental.config.MapperConfig;
import com.carrental.dto.car.CarRequestDto;
import com.carrental.dto.car.CarResponseDto;
import com.carrental.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {

    CarResponseDto toDto(Car car);

    Car toEntity(CarRequestDto requestDto);

    void updateFromDto(CarRequestDto dto, @MappingTarget Car car);
}
