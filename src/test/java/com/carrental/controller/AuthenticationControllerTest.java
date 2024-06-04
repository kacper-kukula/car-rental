package com.carrental.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrental.dto.user.UserLoginRequestDto;
import com.carrental.dto.user.UserLoginResponseDto;
import com.carrental.dto.user.UserRegistrationRequestDto;
import com.carrental.dto.user.UserResponseDto;
import com.carrental.model.User;
import com.carrental.security.AuthenticationService;
import com.carrental.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationRequestDto userRegistrationRequestDto;
    private UserResponseDto userResponseDto;
    private UserLoginRequestDto userLoginRequestDto;
    private UserLoginResponseDto userLoginResponseDto;

    @BeforeEach
    void setUp() {
        userRegistrationRequestDto = new UserRegistrationRequestDto(
                "testuser@rental.com", "password", "password", "Test", "User");

        userResponseDto = new UserResponseDto(
                1L, "testuser@rental.com", "Test", "User", User.Role.CUSTOMER);

        userLoginRequestDto = new UserLoginRequestDto(
                "testuser@rental.com", "password");

        userLoginResponseDto = new UserLoginResponseDto("jwt-token");
    }

    @Test
    @DisplayName("Register new user successfully")
    void register_NewUser_ReturnsUserResponseDto() throws Exception {
        // Given
        Mockito.when(userService.register(Mockito.any(UserRegistrationRequestDto.class)))
                .thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDto.id()))
                .andExpect(jsonPath("$.email").value(userResponseDto.email()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.lastName()))
                .andExpect(jsonPath("$.role").value(userResponseDto.role().toString()));
    }

    @Test
    @DisplayName("Register user with invalid data")
    void register_InvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UserRegistrationRequestDto invalidRequest = new UserRegistrationRequestDto(
                "invalid-email", "short", "short", "Test", "User");

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login existing user successfully")
    void login_ExistingUser_ReturnsUserLoginResponseDto() throws Exception {
        // Given
        Mockito.when(authenticationService.authenticate(Mockito.any(UserLoginRequestDto.class)))
                .thenReturn(userLoginResponseDto);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(userLoginResponseDto.token()));
    }

    @Test
    @DisplayName("Login with invalid credentials")
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        Mockito.when(authenticationService.authenticate(Mockito.any(UserLoginRequestDto.class)))
                .thenThrow(new UsernameNotFoundException("User not found."));

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDto)))
                .andExpect(status().isUnauthorized());
    }
}
