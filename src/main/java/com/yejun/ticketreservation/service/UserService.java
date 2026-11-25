package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.domain.ReservationStatus;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public UserResponseDto getUser(Long userId, Long currentUserId, String role) {

        validateUserAccess(userId, currentUserId, role);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found: " + userId));

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

        String passwordHash = passwordEncoder.encode(userCreateRequestDto.getPassword());

        User user = User.createUser(userCreateRequestDto.getName(), userCreateRequestDto.getEmail(), passwordHash);

        user = userRepository.save(user);

        return UserMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long userId, Long currentUserId, String role) {

        validateUserAccess(userId, currentUserId, role);

        User user = userRepository.findByIdForAccountUpdate(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (reservationRepository.existsByUserIdAndStatus(userId, ReservationStatus.CONFIRMED)) {

            throw new UserHasReservationsException(
                    "Cancel active reservations before deleting your account");
        }

        user.deactivate();
        refreshTokenService.revokeAllByUserId(userId);
    }

    private void validateUserAccess(Long userId, Long currentUserId, String role) {

        if (!userId.equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
