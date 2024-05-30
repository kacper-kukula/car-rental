package com.carrental.controller;

import com.carrental.dto.car.CarRequestDto;
import com.carrental.dto.car.CarResponseDto;
import com.carrental.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car Management",
        description = "Endpoints for managing car inventory (CRUD)")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/cars")
public class CarController {

    private final CarService carService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a car",
            description = "Add a new car to the available cars")
    public CarResponseDto createCar(@RequestBody @Valid CarRequestDto request) {
        return carService.createCar(request);
    }

    @GetMapping
    @Operation(summary = "Get all cars",
            description = "Get a list of all available cars")
    public List<CarResponseDto> findAllCars() {
        return carService.findAllCars();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a car by ID",
            description = "Get a specific car and it's detailed information")
    public CarResponseDto findCarById(@PathVariable Long id) {
        return carService.findCarById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update a car by ID",
            description = "Update a specific car including it's inventory")
    public CarResponseDto updateCarById(@PathVariable Long id,
                                        @RequestBody @Valid CarRequestDto request) {
        return carService.updateCarById(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete a car",
            description = "Soft delete a car by it's ID")
    public ResponseEntity<String> deleteCarById(@PathVariable Long id) {
        carService.deleteCarById(id);
        return ResponseEntity.ok("Car (ID: " + id + ") successfully deleted.");
    }
}
