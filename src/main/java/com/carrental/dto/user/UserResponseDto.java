package com.carrental.dto.user;

import com.carrental.model.User;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        User.Role role
) {
}
