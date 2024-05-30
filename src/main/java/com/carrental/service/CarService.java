package com.carrental.service;

import com.carrental.dto.car.CarCreateRequestDto;
import com.carrental.dto.car.CarResponseDto;
import com.carrental.dto.car.CarUpdateRequestDto;
import java.util.List;

public interface CarService {

    CarResponseDto createCar(CarCreateRequestDto request);

    List<CarResponseDto> findAllCars();

    CarResponseDto findCarById(Long id);

    CarResponseDto updateCarById(Long id, CarUpdateRequestDto request);

    void deleteCarById(Long id);
}
