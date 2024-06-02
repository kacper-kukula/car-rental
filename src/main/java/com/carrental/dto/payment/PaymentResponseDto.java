package com.carrental.dto.payment;

import com.carrental.model.Payment;
import java.math.BigDecimal;

public record PaymentResponseDto(
        Long id,
        Long rentalId,
        BigDecimal amountToPay,
        String sessionUrl,
        String sessionId,
        Payment.Status status,
        Payment.Type type
) {
}
