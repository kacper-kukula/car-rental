package com.carrental.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.carrental.model.Payment;
import java.sql.Connection;
import java.sql.SQLException;
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

        // When
        List<Payment> actual = paymentRepository.findByUserId(userId);

        // Then
        assertEquals(1, actual.size());
        assertEquals(userId, actual.get(0).getRental().getUser().getId());
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

        // When
        Optional<Payment> actual = paymentRepository.findByRentalIdAndType(rentalId, type);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(rentalId, actual.get().getRental().getId());
        assertEquals(type, actual.get().getType());
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
}
