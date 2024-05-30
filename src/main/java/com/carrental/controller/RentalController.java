package com.carrental.controller;

import com.carrental.dto.rental.RentalRequestDto;
import com.carrental.dto.rental.RentalResponseDto;
import com.carrental.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car Rentals Management",
        description = "Endpoints for managing user's car rentals")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rentals")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new rental",
            description = "Add a new rental and decrease car inventory by 1")
    public RentalResponseDto createRental(@RequestBody @Valid RentalRequestDto request) {
        return rentalService.createRental(request);
    }

    @GetMapping
    @Operation(summary = "Get rentals by criteria",
            description = "Get rentals by user ID and whether they are active or not")
    public List<RentalResponseDto> findRentalsByUserIdAndStatus(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") boolean isActive) {
        return rentalService.findRentalsByUserIdAndStatus(userId, isActive);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a rental by ID",
            description = "Get a specific rental and it's detailed information")
    public RentalResponseDto findRentalById(@PathVariable Long id) {
        return rentalService.findRentalById(id);
    }

    @PostMapping("/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return a rental car",
            description = "Return a car and increase inventory by 1")
    public void returnRental(@PathVariable Long id) {
        rentalService.returnRental(id);
    }
}
