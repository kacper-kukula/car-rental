package com.carrental.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.carrental.model.Car;
import com.carrental.model.Payment;
import com.carrental.model.Rental;
import com.carrental.model.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/payment/add-mock-test-payments-rentals-users.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/payment/delete-all-payments-rentals-users.sql")
            );
        }
    }

    @Test
    @DisplayName("Find payments by user ID")
    void findByUserId_ValidUserId_ReturnsTwoPayments() {
        // Given
        Long userId = 999L;
        Payment expected = getExpectedPayment();

        // When
        List<Payment> actual = paymentRepository.findByUserId(userId);

        // Then
        assertEquals(1, actual.size());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("Return empty list given invalid user ID")
    void findByUserId_InvalidUserId_ReturnsEmptyList() {
        // Given
        Long invalidUserId = -1L;

        // When
        List<Payment> actual = paymentRepository.findByUserId(invalidUserId);

        // Then
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find payment by rental ID and type")
    void findByRentalIdAndType_ValidRentalIdAndType_ReturnsPayment() {
        // Given
        Long rentalId = 777L;
        Payment.Type type = Payment.Type.PAYMENT;
        Payment expected = getExpectedPayment();

        // When
        Optional<Payment> actual = paymentRepository.findByRentalIdAndType(rentalId, type);

        // Then
        assertTrue(actual.isPresent());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("Return empty optional given invalid rental ID and type")
    void findByRentalIdAndType_InvalidRentalIdAndType_ReturnsEmptyOptional() {
        // Given
        Long invalidRentalId = -1L;
        Payment.Type invalidType = Payment.Type.PAYMENT;

        // When
        Optional<Payment> actual =
                paymentRepository.findByRentalIdAndType(invalidRentalId, invalidType);

        // Then
        assertTrue(actual.isEmpty());
    }

    private Payment getExpectedPayment() {
        Payment payment = new Payment();
        payment.setId(666L);
        payment.setRental(getExpectedRental());
        payment.setSessionUrl("url");
        payment.setSessionId("id");
        payment.setAmountToPay(BigDecimal.valueOf(2800.00));
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);

        return payment;
    }

    private Rental getExpectedRental() {
        Rental rental = new Rental();
        rental.setId(777L);
        rental.setUser(getExpectedUser());
        rental.setCar(getExpectedCar());
        rental.setRentalDate(LocalDate.of(2024, 6, 1));
        rental.setReturnDate(LocalDate.of(2024, 6, 8));
        rental.setStatus(Rental.Status.ACTIVE);

        return rental;
    }

    private User getExpectedUser() {
        User user = new User();
        user.setId(999L);
        user.setEmail("testmanager@rental.com");
        user.setPassword("$2a$10$hLSU4qNrQO6lItn7H/cALuq0YvqLUmAVZ5qlKLsmigSV7FberE8v2");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.Role.MANAGER);

        return user;
    }

    private Car getExpectedCar() {
        Car car = new Car();
        car.setId(888L);
        car.setModel("XM");
        car.setBrand("BMW");
        car.setType(Car.Type.SUV);
        car.setInventory(2);
        car.setDailyFee(BigDecimal.valueOf(400.00));

        return car;
    }

}
