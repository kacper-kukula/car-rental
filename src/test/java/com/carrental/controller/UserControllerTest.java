package com.carrental.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrental.dto.user.RoleUpdateRequestDto;
import com.carrental.dto.user.UserResponseDto;
import com.carrental.dto.user.UserUpdateRequestDto;
import com.carrental.model.User;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private RoleUpdateRequestDto roleUpdateRequestDto;
    private UserResponseDto userResponseDto;
    private UserUpdateRequestDto userUpdateRequestDto;

    @BeforeEach
    void setUp() {
        roleUpdateRequestDto = new RoleUpdateRequestDto(User.Role.CUSTOMER);

        userResponseDto = new UserResponseDto(
                1L, "testuser@rental.com", "Test", "User", User.Role.CUSTOMER);

        userUpdateRequestDto = new UserUpdateRequestDto("New Name", "New Surname");
    }

    @Test
    @DisplayName("Set user role successfully")
    @WithMockUser(roles = {"MANAGER"})
    void setRole_UserRoleSet_ReturnsUserResponseDto() throws Exception {
        // Given
        Mockito.when(userService.setRole(Mockito.anyLong(),
                        Mockito.any(RoleUpdateRequestDto.class)))
                .thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(put("/users/{id}/role", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.id()))
                .andExpect(jsonPath("$.email").value(userResponseDto.email()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.lastName()))
                .andExpect(jsonPath("$.role").value(userResponseDto.role().toString()));
    }

    @Test
    @DisplayName("Get user profile successfully")
    @WithMockUser(roles = {"CUSTOMER"})
    void getProfile_UserProfileRetrieved_ReturnsUserResponseDto() throws Exception {
        // Given
        Mockito.when(userService.getProfile()).thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.id()))
                .andExpect(jsonPath("$.email").value(userResponseDto.email()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.lastName()))
                .andExpect(jsonPath("$.role").value(userResponseDto.role().toString()));
    }

    @Test
    @DisplayName("Update user profile successfully")
    @WithMockUser(roles = {"CUSTOMER"})
    void updateProfile_UserProfileUpdated_ReturnsUserResponseDto() throws Exception {
        // Given
        Mockito.when(userService.updateProfile(Mockito.any(UserUpdateRequestDto.class)))
                .thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.id()))
                .andExpect(jsonPath("$.email").value(userResponseDto.email()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.lastName()))
                .andExpect(jsonPath("$.role").value(userResponseDto.role().toString()));
    }
}
