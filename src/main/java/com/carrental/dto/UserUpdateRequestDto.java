package com.carrental.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {
}
