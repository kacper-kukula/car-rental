package com.carrental.service.impl;

import com.carrental.dto.RoleRequestDto;
import com.carrental.dto.UserRegistrationRequestDto;
import com.carrental.dto.UserResponseDto;
import com.carrental.dto.UserUpdateRequestDto;
import com.carrental.exception.RegistrationException;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
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
    public UserResponseDto setRole(Long id, RoleRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setRole(request.role());
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto getProfile() {
        return userMapper.toDto(getCurrentUserFromDb());
    }

    @Override
    @Transactional
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
