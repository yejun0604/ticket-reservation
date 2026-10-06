package com.yejun.ticketreservation.mapper;

import com.yejun.ticketreservation.domain.User;
import com.yejun.ticketreservation.dto.UserCreateRequestDto;
import com.yejun.ticketreservation.dto.UserResponseDto;

public class UserMapper {

    public static UserResponseDto toDto(User user){

        UserResponseDto userDto  = new UserResponseDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public static User toEntity(UserCreateRequestDto userRequestDto){

        User user = new User();

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());

        return user;
    }
}
