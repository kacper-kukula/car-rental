package com.carrental.dto.car;

import com.carrental.model.Car;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CarRequestDto(
        @NotBlank
        String model,

        @NotBlank
        String brand,

        @PositiveOrZero
        int inventory,

        @PositiveOrZero
        BigDecimal dailyFee,

        @NotNull
        Car.Type type
) {
}
