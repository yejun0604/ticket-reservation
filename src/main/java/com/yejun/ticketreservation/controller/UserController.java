package com.yejun.ticketreservation.controller;

import com.yejun.ticketreservation.dto.UserCreateRequestDto;
import com.yejun.ticketreservation.dto.UserResponseDto;
import com.yejun.ticketreservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal Jwt jwt) {

        Long currentUserId = Long.valueOf(jwt.getSubject());

        String role = jwt.getClaimAsString("role");

        return ResponseEntity.ok(
                userService.getUser(userId, currentUserId, role)
        );
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers() {

        List<UserResponseDto> users = userService.getUsers();

        return ResponseEntity.ok().body(users);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {

        UserResponseDto user = userService.createUser(userCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/{userId}") public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal Jwt jwt) {

        Long currentUserId = Long.valueOf(jwt.getSubject());

        String role = jwt.getClaimAsString("role");

        userService.deleteUser(userId, currentUserId, role);

        return ResponseEntity.noContent().build();
    }
}
