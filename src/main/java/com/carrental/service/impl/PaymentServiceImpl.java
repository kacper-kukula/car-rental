package com.carrental.service.impl;

import com.carrental.dto.payment.PaymentCreateRequestDto;
import com.carrental.dto.payment.PaymentPausedResponseDto;
import com.carrental.dto.payment.PaymentResponseDto;
import com.carrental.exception.custom.StripeSessionException;
import com.carrental.mapper.PaymentMapper;
import com.carrental.model.Payment;
import com.carrental.model.Rental;
import com.carrental.repository.PaymentRepository;
import com.carrental.repository.RentalRepository;
import com.carrental.security.util.AuthenticationUtil;
import com.carrental.service.NotificationService;
import com.carrental.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String CURRENCY = "usd";
    private static final String SUCCESS_URL =
            "http://localhost:8080/api/payments/success/%s?type=%s";
    private static final String CANCEL_URL = "http://localhost:8080/api/payments/cancel/";
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.50");
    private static final BigDecimal TO_CENTS_MULTIPLIER = new BigDecimal("100");
    private static final String PAYMENT_NAME = "Car Rental ID ";
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;
    private final AuthenticationUtil authenticationUtil;
    private final NotificationService notificationService;
    private final Dotenv dotenv;

    @PostConstruct
    public void init() {
        Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");
    }

    @Override
    @Transactional
    public PaymentResponseDto createPaymentSession(PaymentCreateRequestDto request) {
        Rental rental = rentalRepository.findById(request.rentalId())
                .orElseThrow(() -> new EntityNotFoundException("Rental does not exist."));
        BigDecimal amountToPay = calculateAmountToPay(rental, request.type());

        SessionCreateParams.Builder builder = new SessionCreateParams.Builder();
        builder.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD);
        builder.setMode(SessionCreateParams.Mode.PAYMENT);
        builder.setSuccessUrl(UriComponentsBuilder.fromHttpUrl(
                SUCCESS_URL.formatted(rental.getId(), request.type())).toUriString());
        builder.setCancelUrl(UriComponentsBuilder.fromHttpUrl(
                CANCEL_URL + rental.getId()).toUriString());
        builder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(CURRENCY)
                                        .setUnitAmount(amountToPay.multiply(
                                                TO_CENTS_MULTIPLIER).longValue())
                                        .setProductData(
                                                SessionCreateParams
                                                        .LineItem
                                                        .PriceData
                                                        .ProductData
                                                        .builder()
                                                        .setName(PAYMENT_NAME + rental.getId())
                                                        .build())
                                        .build())
                        .build());

        SessionCreateParams createParams = builder.build();
        Session session;

        try {
            session = Session.create(createParams);
        } catch (StripeException e) {
            throw new StripeSessionException("Error creating Stripe session.");
        }

        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(request.type());
        payment.setAmountToPay(amountToPay);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());

        Payment savedPayment = paymentRepository.save(payment);
        PaymentResponseDto dto = paymentMapper.toDto(savedPayment);

        notificationService.createPaymentMessage(dto);

        return dto;
    }

    @Override
    @Transactional
    public List<PaymentResponseDto> findPayments(Long userId) {
        boolean isManager = authenticationUtil.isManager();

        if (!isManager && userId != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Customers are not authorized to specify a user ID.");
        }

        List<Payment> payments;
        if (!isManager) {
            payments = paymentRepository.findByUserId(
                    authenticationUtil.getCurrentUserFromDb().getId());
        } else {
            payments = (userId != null)
                    ? paymentRepository.findByUserId(userId) : paymentRepository.findAll();
        }

        return payments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponseDto checkSuccessfulPayment(Long rentalId, Payment.Type type) {
        Payment payment = paymentRepository.findByRentalIdAndType(rentalId, type)
                .orElseThrow(() -> new EntityNotFoundException("Payment does not exist."));

        Session session;

        try {
            session = Session.retrieve(payment.getSessionId());
        } catch (StripeException e) {
            throw new StripeSessionException("Error while retrieving session.");
        }

        if (session.getPaymentStatus().equals("paid")) {
            payment.setStatus(Payment.Status.PAID);
            Payment savedPayment = paymentRepository.save(payment);

            PaymentResponseDto dto = paymentMapper.toDto(savedPayment);

            notificationService.paidPaymentMessage(dto);

            return dto;
        }

        throw new StripeSessionException("Payment is not paid.");
    }

    @Override
    public PaymentPausedResponseDto returnPausedPayment(Long rentalId) {
        return new PaymentPausedResponseDto(
                """
                        Payment has been paused, but can be made later.
                        Please note that your session will remain active for 24 hours.
                        After that time, it will expire.""");
    }

    private BigDecimal calculateAmountToPay(Rental rental, Payment.Type paymentType) {
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        long totalDays = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        BigDecimal amount = dailyFee.multiply(BigDecimal.valueOf(totalDays));

        if (paymentType == Payment.Type.FINE) {
            amount = amount.multiply(FINE_MULTIPLIER);
        }

        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
