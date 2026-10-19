package com.yejun.ticketreservation.mapper;

import com.yejun.ticketreservation.domain.User;
import com.yejun.ticketreservation.dto.UserCreateRequestDto;
import com.yejun.ticketreservation.dto.UserResponseDto;

public class UserMapper {

    public static UserResponseDto toDto(User user){

        UserResponseDto userResponseDto  = new UserResponseDto();

        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());

        return userResponseDto;
    }

    public static User toEntity(UserCreateRequestDto userRequestDto){

        User user = new User();

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());

        return user;
    }
}
