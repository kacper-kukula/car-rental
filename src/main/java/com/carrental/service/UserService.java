package com.carrental.service;

import com.carrental.dto.RoleRequestDto;
import com.carrental.dto.UserRegistrationRequestDto;
import com.carrental.dto.UserResponseDto;
import com.carrental.dto.UserUpdateRequestDto;
import com.carrental.exception.RegistrationException;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto setRole(Long id, RoleRequestDto request);

    UserResponseDto getProfile();

    UserResponseDto updateProfile(UserUpdateRequestDto request);
}
