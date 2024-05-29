package com.carrental.mapper;

import com.carrental.config.MapperConfig;
import com.carrental.dto.UserRegistrationRequestDto;
import com.carrental.dto.UserResponseDto;
import com.carrental.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(UserRegistrationRequestDto requestDto);
}
