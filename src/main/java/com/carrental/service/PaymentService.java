package com.carrental.service;

import com.carrental.dto.payment.PaymentCreateRequestDto;
import com.carrental.dto.payment.PaymentPausedResponseDto;
import com.carrental.dto.payment.PaymentResponseDto;
import com.carrental.model.Payment;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(PaymentCreateRequestDto request);

    List<PaymentResponseDto> findPayments(Long userId);

    PaymentResponseDto checkSuccessfulPayment(Long rentalId, Payment.Type type);

    PaymentPausedResponseDto returnPausedPayment(Long rentalId);
}
