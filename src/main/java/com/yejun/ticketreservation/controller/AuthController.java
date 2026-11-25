package com.yejun.ticketreservation.controller;

import com.yejun.ticketreservation.dto.LoginRequestDto;
import com.yejun.ticketreservation.dto.LoginResponseDto;
import com.yejun.ticketreservation.dto.RefreshTokenRequestDto;
import com.yejun.ticketreservation.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            @Valid @RequestBody RefreshTokenRequestDto request) {

        return ResponseEntity.ok(
                authService.refresh(request.getRefreshToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequestDto request) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.noContent().build();
    }
}