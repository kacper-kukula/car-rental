package com.carrental.service;

import com.carrental.dto.payment.PaymentResponseDto;
import com.carrental.model.Car;
import com.carrental.model.Rental;

public interface NotificationService {

    void createRentalMessage(Rental rental, Car car);

    void createPaymentMessage(PaymentResponseDto dto);

    void paidPaymentMessage(PaymentResponseDto dto);

    void checkOverdueRentals();
}
