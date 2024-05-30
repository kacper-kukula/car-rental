package com.carrental.dto.user;

import com.carrental.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequestDto(
        @NotNull
        @Enumerated(EnumType.STRING)
        User.Role role
) {
}
