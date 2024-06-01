package com.carrental.dto.payment;

import com.carrental.model.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentCreateRequestDto(
        @Positive
        Long rentalId,

        @NotNull
        Payment.Type type
) {
}
