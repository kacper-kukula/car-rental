package com.carrental.dto.user;

import com.carrental.model.User;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequestDto(
        @NotNull
        User.Role role
) {
}
