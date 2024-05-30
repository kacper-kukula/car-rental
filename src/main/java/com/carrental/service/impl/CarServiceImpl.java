package com.carrental.service.impl;

import com.carrental.dto.car.CarCreateRequestDto;
import com.carrental.dto.car.CarResponseDto;
import com.carrental.dto.car.CarUpdateRequestDto;
import com.carrental.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    @Override
    public CarResponseDto createCar(CarCreateRequestDto request) {
        return null;
    }

    @Override
    public List<CarResponseDto> findAllCars() {
        return null;
    }

    @Override
    public CarResponseDto findCarById(Long id) {
        return null;
    }

    @Override
    public CarResponseDto updateCarById(Long id, CarUpdateRequestDto request) {
        return null;
    }

    @Override
    public void deleteCarById(Long id) {

    }
}
