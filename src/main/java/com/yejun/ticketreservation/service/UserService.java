package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.domain.User;
import com.yejun.ticketreservation.dto.UserCreateRequestDto;
import com.yejun.ticketreservation.dto.UserResponseDto;
import com.yejun.ticketreservation.exception.EmailAlreadyExistsException;
import com.yejun.ticketreservation.exception.UserHasReservationsException;
import com.yejun.ticketreservation.exception.UserNotFoundException;
import com.yejun.ticketreservation.mapper.UserMapper;
import com.yejun.ticketreservation.repository.ReservationRepository;
import com.yejun.ticketreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public UserResponseDto getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found: " + userId)
                );

        return UserMapper.toDto(user);
    }

    public List<UserResponseDto> getUsers(){

        List<User> users = userRepository.findAll();

        return users
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Transactional
    public UserResponseDto createUser(UserCreateRequestDto userCreateRequestDto){

        // Check for duplicate email
        if(userRepository.existsByEmail(userCreateRequestDto.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists: " + userCreateRequestDto.getEmail());
        }

        User user = userRepository.save(UserMapper.toEntity(userCreateRequestDto));

        return UserMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long userId ){

        User user = userRepository.findById(userId )
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId ));

        if(reservationRepository.existsByUserId(userId )) {
            throw new UserHasReservationsException("User with reservations cannot be deleted: " + userId);
        }

        userRepository.delete(user);
    }
}
