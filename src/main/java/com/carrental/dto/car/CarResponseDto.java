package com.carrental.dto.car;

import com.carrental.model.Car;
import java.math.BigDecimal;

public record CarResponseDto(
        Long id,
        String model,
        String brand,
        int inventory,
        BigDecimal dailyFee,
        Car.Type type
) {
}
