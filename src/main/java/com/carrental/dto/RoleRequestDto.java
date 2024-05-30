package com.carrental.dto;

import com.carrental.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record RoleRequestDto(
        @NotNull
        @Enumerated(EnumType.STRING)
        User.Role role
) {
}
