package com.carrental.dto.rental;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record RentalRequestDto(

        @Positive
        Long carId,

        @Future
        LocalDate returnDate
) {
}
