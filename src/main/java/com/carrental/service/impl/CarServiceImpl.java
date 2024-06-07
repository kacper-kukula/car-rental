package com.carrental.service.impl;

import com.carrental.dto.car.CarRequestDto;
import com.carrental.dto.car.CarResponseDto;
import com.carrental.mapper.CarMapper;
import com.carrental.model.Car;
import com.carrental.repository.CarRepository;
import com.carrental.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private static final String CAR_NOT_FOUND_MESSAGE = "Car not found.";

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarResponseDto createCar(CarRequestDto request) {
        Car car = carMapper.toEntity(request);
        Car savedCar = carRepository.save(car);

        return carMapper.toDto(savedCar);
    }

    @Override
    public List<CarResponseDto> findAllCars(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarResponseDto findCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_MESSAGE));

        return carMapper.toDto(car);
    }

    @Override
    public CarResponseDto updateCarById(Long id, CarRequestDto request) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CAR_NOT_FOUND_MESSAGE));

        carMapper.updateFromDto(request, car);
        Car updatedCar = carRepository.save(car);

        return carMapper.toDto(updatedCar);
    }

    @Override
    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }
}
