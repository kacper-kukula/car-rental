package com.carrental.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.carrental.model.User;
import java.sql.Connection;
import java.sql.SQLException;
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
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/user/add-mock-test-users.sql")
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
                            "database/user/delete-all-users.sql")
            );
        }
    }

    @Test
    @DisplayName("Find manager given valid email")
    void findByEmail_ValidUserEmail_ReturnsManager() {
        // Given
        String managerEmail = "testmanager@rental.com";
        User.Role role = User.Role.MANAGER;

        // When
        Optional<User> actual = userRepository.findByEmail(managerEmail);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(managerEmail, actual.get().getEmail());
        assertEquals(role, actual.get().getRole());
    }

    @Test
    @DisplayName("Return empty optional given invalid email")
    void findByEmail_NonExistentUserEmail_ReturnsEmptyOptional() {
        // Given
        String nonExistentEmail = "testnonexistentemail@rental.com";

        // When
        Optional<User> actual = userRepository.findByEmail(nonExistentEmail);

        // Then
        assertTrue(actual.isEmpty());
    }
}
