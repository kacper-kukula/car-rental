package com.carrental.service;

import com.carrental.dto.UserRegistrationRequestDto;
import com.carrental.dto.UserResponseDto;
import com.carrental.exception.RegistrationException;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
