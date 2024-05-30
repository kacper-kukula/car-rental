package com.carrental.dto.rental;

import java.time.LocalDate;

public record RentalResponseDto(
        Long id,
        Long userId,
        Long carId,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate
) {
}
