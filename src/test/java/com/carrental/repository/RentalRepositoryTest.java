package com.carrental.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.carrental.model.Car;
import com.carrental.model.Rental;
import com.carrental.model.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
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
public class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/rental/add-mock-test-rentals-cars-users.sql")
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
                            "database/rental/delete-all-rentals-cars-users.sql")
            );
        }
    }

    @Test
    @DisplayName("Find rentals by user ID and status")
    void findByUserIdAndStatus_ValidUserIdAndStatus_ReturnsRentals() {
        // Given
        Long userId = 999L;
        Rental.Status status = Rental.Status.ACTIVE;
        Rental expected = getExpectedRental();

        // When
        List<Rental> actual = rentalRepository.findByUserIdAndStatus(userId, status);

        // Then
        assertEquals(1, actual.size());
        EqualsBuilder.reflectionEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Return empty list given invalid user ID and status")
    void findByUserIdAndStatus_InvalidUserIdAndStatus_ReturnsEmptyList() {
        // Given
        Long invalidUserId = -1L;
        Rental.Status status = Rental.Status.ACTIVE;

        // When
        List<Rental> actual = rentalRepository.findByUserIdAndStatus(invalidUserId, status);

        // Then
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find rentals by status")
    void findByStatus_ValidStatus_ReturnsRentals() {
        // Given
        Rental.Status status = Rental.Status.ACTIVE;
        Rental expected = getExpectedRental();

        // When
        List<Rental> actual = rentalRepository.findByStatus(status);

        // Then
        assertEquals(1, actual.size());
        EqualsBuilder.reflectionEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Return empty list given invalid status")
    void findByStatus_InvalidStatus_ReturnsEmptyList() {
        // Given
        Rental.Status invalidStatus = Rental.Status.RETURNED;

        // When
        List<Rental> actual = rentalRepository.findByStatus(invalidStatus);

        // Then
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find rentals by return date range and status")
    void findAllByReturnDateBetweenAndStatus_ValidDateRangeAndStatus_ReturnsRentals() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 10);
        Rental.Status status = Rental.Status.ACTIVE;
        Rental expected = getExpectedRental();

        // When
        List<Rental> actual =
                rentalRepository.findAllByReturnDateBetweenAndStatus(startDate, endDate, status);

        // Then
        assertEquals(1, actual.size());
        EqualsBuilder.reflectionEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("Return empty list given invalid date range and status")
    void findAllByReturnDateBetweenAndStatus_InvalidDateRangeAndStatus_ReturnsEmptyList() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        Rental.Status status = Rental.Status.ACTIVE;

        // When
        List<Rental> actual =
                rentalRepository.findAllByReturnDateBetweenAndStatus(startDate, endDate, status);

        // Then
        assertTrue(actual.isEmpty());
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
