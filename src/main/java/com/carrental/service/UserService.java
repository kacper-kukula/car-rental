package com.carrental.service;

import com.carrental.dto.user.RoleUpdateRequestDto;
import com.carrental.dto.user.UserRegistrationRequestDto;
import com.carrental.dto.user.UserResponseDto;
import com.carrental.dto.user.UserUpdateRequestDto;
import com.carrental.exception.custom.RegistrationException;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto setRole(Long id, RoleUpdateRequestDto request);

    UserResponseDto getProfile();

    UserResponseDto updateProfile(UserUpdateRequestDto request);
}
