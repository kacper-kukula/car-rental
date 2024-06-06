package com.carrental.service;

import com.carrental.dto.car.CarRequestDto;
import com.carrental.dto.car.CarResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {

    CarResponseDto createCar(CarRequestDto request);

    List<CarResponseDto> findAllCars(Pageable pageable);

    CarResponseDto findCarById(Long id);

    CarResponseDto updateCarById(Long id, CarRequestDto request);

    void deleteCarById(Long id);
}
