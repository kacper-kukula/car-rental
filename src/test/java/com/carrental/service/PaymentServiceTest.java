package com.carrental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.carrental.dto.payment.PaymentCreateRequestDto;
import com.carrental.dto.payment.PaymentResponseDto;
import com.carrental.exception.custom.StripeSessionException;
import com.carrental.mapper.PaymentMapper;
import com.carrental.model.Car;
import com.carrental.model.Payment;
import com.carrental.model.Rental;
import com.carrental.repository.PaymentRepository;
import com.carrental.repository.RentalRepository;
import com.carrental.security.util.AuthenticationUtil;
import com.carrental.service.impl.PaymentServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Dotenv dotenv;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Verify that createPaymentSession() works with valid request")
    void createPaymentSession_ValidRequest_ReturnsCorrectResponse() throws StripeException {
        // Given
        PaymentCreateRequestDto requestDto = new PaymentCreateRequestDto(1L, Payment.Type.PAYMENT);
        Rental rental = getDummyRental();
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(requestDto.type());
        payment.setAmountToPay(BigDecimal.valueOf(50.00));
        payment.setSessionUrl("dummyUrl");
        payment.setSessionId("dummySessionId");
        PaymentResponseDto expectedResponseDto = new PaymentResponseDto(1L, rental.getId(),
                payment.getAmountToPay(), payment.getSessionUrl(), payment.getSessionId(),
                payment.getStatus(), payment.getType());

        when(rentalRepository.findById(requestDto.rentalId())).thenReturn(Optional.of(rental));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expectedResponseDto);
        doNothing().when(notificationService).createPaymentMessage(expectedResponseDto);

        Session session = mock(Session.class);
        when(session.getId()).thenReturn("dummySessionId");
        when(session.getUrl()).thenReturn("dummyUrl");

        // When
        try (MockedStatic<Session> dummyStatic = mockStatic(Session.class)) {
            when(Session.create(any(SessionCreateParams.class))).thenReturn(session);

            PaymentResponseDto actual = paymentService.createPaymentSession(requestDto);

            // Then
            assertThat(actual).isEqualTo(expectedResponseDto);
            verify(rentalRepository, times(1)).findById(requestDto.rentalId());
            verify(paymentRepository, times(1)).save(any(Payment.class));
            verify(paymentMapper, times(1)).toDto(payment);
            verify(notificationService, times(1)).createPaymentMessage(expectedResponseDto);
            verifyNoMoreInteractions(rentalRepository, paymentRepository, paymentMapper);
        }
    }

    @Test
    @DisplayName("Verify that createPaymentSession() throws StripeSessionException")
    void createPaymentSession_StripeException_ThrowsStripeSessionException() {
        // Given
        PaymentCreateRequestDto requestDto = new PaymentCreateRequestDto(1L, Payment.Type.PAYMENT);
        Rental rental = getDummyRental();

        when(rentalRepository.findById(requestDto.rentalId())).thenReturn(Optional.of(rental));

        // When
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new StripeSessionException("Test Stripe Exception"));

            // Then
            assertThrows(StripeSessionException.class,
                    () -> paymentService.createPaymentSession(requestDto));

            verify(rentalRepository, times(1)).findById(requestDto.rentalId());
            verifyNoMoreInteractions(rentalRepository, paymentRepository, paymentMapper);
        }
    }

    @Test
    @DisplayName("Verify that findPayments() works with valid user ID")
    void findPayments_ValidUserId_ReturnsPayments() {
        // Given
        final Long userId = 1L;
        Payment payment1 = getDummyPayment();
        Payment payment2 = getDummyPayment();
        payment2.setId(2L);

        PaymentResponseDto responseDto1 = new PaymentResponseDto(
                1L,
                payment1.getRental().getId(),
                payment1.getAmountToPay(),
                payment1.getSessionUrl(),
                payment1.getSessionId(),
                payment1.getStatus(),
                payment1.getType()
        );
        PaymentResponseDto responseDto2 = new PaymentResponseDto(
                2L,
                payment2.getRental().getId(),
                payment2.getAmountToPay(),
                payment2.getSessionUrl(),
                payment2.getSessionId(),
                payment2.getStatus(),
                payment2.getType()
        );

        List<Payment> payments = List.of(payment1, payment2);
        List<PaymentResponseDto> expected = List.of(responseDto1, responseDto2);

        when(authenticationUtil.isManager()).thenReturn(true);
        when(paymentRepository.findByUserId(userId)).thenReturn(payments);
        when(paymentMapper.toDto(payment1)).thenReturn(responseDto1);
        when(paymentMapper.toDto(payment2)).thenReturn(responseDto2);

        // When
        List<PaymentResponseDto> actual = paymentService.findPayments(userId);

        // Then
        assertThat(actual).containsExactlyElementsOf(expected);
        verify(paymentRepository, times(1)).findByUserId(userId);
        verify(paymentMapper, times(1)).toDto(payment1);
        verify(paymentMapper, times(1)).toDto(payment2);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("Verify that checkSuccessfulPayment() works with valid session")
    void checkSuccessfulPayment_ValidSession_ReturnsPaidPayment() throws StripeException {
        // Given
        final Long rentalId = 1L;
        Payment.Type type = Payment.Type.PAYMENT;
        Payment payment = getDummyPayment();
        payment.setStatus(Payment.Status.PENDING);

        PaymentResponseDto expectedResponseDto = new PaymentResponseDto(
                payment.getId(),
                payment.getRental().getId(),
                payment.getAmountToPay(),
                payment.getSessionUrl(),
                payment.getSessionId(),
                Payment.Status.PAID,
                payment.getType()
        );

        when(paymentRepository.findByRentalIdAndType(rentalId, type))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expectedResponseDto);

        try (MockedStatic<Session> dummyStatic = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            when(session.getPaymentStatus()).thenReturn("paid");
            when(Session.retrieve(payment.getSessionId())).thenReturn(session);

            // When
            PaymentResponseDto actual = paymentService.checkSuccessfulPayment(rentalId, type);

            // Then
            assertThat(actual).isEqualTo(expectedResponseDto);
            verify(paymentRepository, times(1)).findByRentalIdAndType(rentalId, type);
            verify(paymentRepository, times(1)).save(payment);
            verify(paymentMapper, times(1)).toDto(payment);
            verify(notificationService, times(1)).paidPaymentMessage(expectedResponseDto);
            verifyNoMoreInteractions(paymentRepository, paymentMapper);
        }
    }

    private Rental getDummyRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(7));
        rental.setStatus(Rental.Status.ACTIVE);
        rental.setCar(getDummyCar());

        return rental;
    }

    private Payment getDummyPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRental(getDummyRental());
        payment.setStatus(Payment.Status.PAID);
        payment.setAmountToPay(BigDecimal.valueOf(50.00));
        payment.setSessionUrl("dummyUrl");
        payment.setSessionId("dummySessionId");

        return payment;
    }

    private Car getDummyCar() {
        Car car = new Car();
        car.setId(1L);
        car.setModel("A4");
        car.setBrand("Audi");
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(50));
        car.setType(Car.Type.SEDAN);

        return car;
    }
}
