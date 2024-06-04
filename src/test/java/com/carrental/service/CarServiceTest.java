package com.carrental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.carrental.dto.car.CarRequestDto;
import com.carrental.dto.car.CarResponseDto;
import com.carrental.mapper.CarMapper;
import com.carrental.model.Car;
import com.carrental.repository.CarRepository;
import com.carrental.service.impl.CarServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Verify that createCar() method works with valid car")
    void createCar_ValidCarRequestDto_ReturnsCorrectCarResponseDto() {
        // Given
        final CarRequestDto carRequestDto = getDummyCarRequestDto();
        final CarResponseDto carResponseDto = getDummyCarResponseDto();
        Car car = getDummyCar();
        Car savedCar = getDummyCar();
        savedCar.setId(1L);

        when(carMapper.toEntity(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(savedCar);
        when(carMapper.toDto(savedCar)).thenReturn(carResponseDto);

        // When
        CarResponseDto actual = carService.createCar(carRequestDto);

        // Then
        assertThat(actual).isEqualTo(carResponseDto);
        verify(carRepository, times(1)).save(car);
        verify(carMapper, times(1)).toEntity(carRequestDto);
        verify(carMapper, times(1)).toDto(savedCar);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify that findAllCars() method works")
    void findAllCars_ReturnsAllCars() {
        // Given
        Car car1 = getDummyCar();
        car1.setId(1L);
        Car car2 = new Car();
        car2.setId(2L);
        car2.setModel("Civic");
        car2.setBrand("Honda");
        car2.setInventory(5);
        car2.setDailyFee(BigDecimal.valueOf(45));
        car2.setType(Car.Type.SEDAN);

        CarResponseDto carResponseDto1 = getDummyCarResponseDto();
        CarResponseDto carResponseDto2 = new CarResponseDto(
                2L, "Civic", "Honda", 5, BigDecimal.valueOf(45), Car.Type.SEDAN);

        List<Car> cars = List.of(car1, car2);
        List<CarResponseDto> expected = List.of(carResponseDto1, carResponseDto2);

        when(carRepository.findAll()).thenReturn(cars);
        when(carMapper.toDto(car1)).thenReturn(carResponseDto1);
        when(carMapper.toDto(car2)).thenReturn(carResponseDto2);

        // When
        List<CarResponseDto> actual = carService.findAllCars();

        // Then
        assertThat(actual).containsExactlyElementsOf(expected);
        verify(carRepository, times(1)).findAll();
        verify(carMapper, times(1)).toDto(car1);
        verify(carMapper, times(1)).toDto(car2);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify that findCarById() method returns correct car by ID")
    void findCarById_ValidId_ReturnsCorrectCar() {
        // Given
        Long testCarId = 1L;
        Car car = getDummyCar();
        car.setId(testCarId);
        CarResponseDto expectedCarResponseDto = getDummyCarResponseDto();

        when(carRepository.findById(testCarId)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(expectedCarResponseDto);

        // When
        CarResponseDto actual = carService.findCarById(testCarId);

        // Then
        assertThat(actual).isEqualTo(expectedCarResponseDto);
        verify(carRepository, times(1)).findById(testCarId);
        verify(carMapper, times(1)).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify that deleteCarById() method works")
    void deleteCarById_ValidId_CarDeletedSuccessfully() {
        // Given
        Long carId = 1L;

        // When
        carService.deleteCarById(carId);

        // Then
        verify(carRepository, times(1)).deleteById(carId);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify that updateCarById() method updates car")
    void updateCarById_ValidId_ReturnsUpdatedCarResponseDto() {
        // Given
        Long testCarId = 1L;
        final CarRequestDto carRequestDto =
                new CarRequestDto("Camry", "Toyota", 12, BigDecimal.valueOf(50), Car.Type.SEDAN);
        Car existingCar = getDummyCar();
        existingCar.setId(testCarId);
        Car updatedCar = getDummyCar();
        updatedCar.setId(testCarId);
        updatedCar.setModel("Camry");
        updatedCar.setBrand("Toyota");
        updatedCar.setInventory(12);
        final CarResponseDto expectedCarResponseDto = new CarResponseDto(testCarId, "Camry",
                "Toyota", 12, BigDecimal.valueOf(50), Car.Type.SEDAN);

        when(carRepository.findById(testCarId)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(existingCar)).thenReturn(updatedCar);
        when(carMapper.toDto(updatedCar)).thenReturn(expectedCarResponseDto);

        // When
        CarResponseDto actual = carService.updateCarById(testCarId, carRequestDto);

        // Then
        assertThat(actual).isEqualTo(expectedCarResponseDto);
        verify(carRepository, times(1)).findById(testCarId);
        verify(carRepository, times(1)).save(existingCar);
        verify(carMapper, times(1)).toDto(updatedCar);
        verify(carMapper, times(1)).updateFromDto(carRequestDto, existingCar);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    private Car getDummyCar() {
        Car car = new Car();
        car.setModel("A4");
        car.setBrand("Audi");
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(50));
        car.setType(Car.Type.SEDAN);

        return car;
    }

    private CarRequestDto getDummyCarRequestDto() {
        return new CarRequestDto("A4", "Audi", 10, BigDecimal.valueOf(50), Car.Type.SEDAN);
    }

    private CarResponseDto getDummyCarResponseDto() {
        return new CarResponseDto(1L, "A4", "Audi", 10, BigDecimal.valueOf(50), Car.Type.SEDAN);
    }
}
