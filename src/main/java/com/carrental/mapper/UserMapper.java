package com.carrental.mapper;

import com.carrental.config.MapperConfig;
import com.carrental.dto.user.UserRegistrationRequestDto;
import com.carrental.dto.user.UserResponseDto;
import com.carrental.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(UserRegistrationRequestDto requestDto);
}
