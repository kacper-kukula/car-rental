package com.carrental.service.impl;

import com.carrental.dto.user.RoleUpdateRequestDto;
import com.carrental.dto.user.UserRegistrationRequestDto;
import com.carrental.dto.user.UserResponseDto;
import com.carrental.dto.user.UserUpdateRequestDto;
import com.carrental.exception.custom.RegistrationException;
import com.carrental.mapper.UserMapper;
import com.carrental.model.User;
import com.carrental.repository.UserRepository;
import com.carrental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new RegistrationException("Can't register this user.");
        }

        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto setRole(Long id, RoleUpdateRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setRole(request.role());
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserResponseDto getProfile() {
        return userMapper.toDto(getCurrentUserFromDb());
    }

    @Override
    public UserResponseDto updateProfile(UserUpdateRequestDto request) {
        User user = getCurrentUserFromDb();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    private User getCurrentUserFromDb() {
        String username = ((UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUsername();

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    }
}
