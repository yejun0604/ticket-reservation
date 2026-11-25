package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.dto.LoginRequestDto;
import com.yejun.ticketreservation.dto.LoginResponseDto;
import com.yejun.ticketreservation.security.CustomUserDetails;
import com.yejun.ticketreservation.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.jwt.access-token-expiration}")
    private long expiresIn;

    public LoginResponseDto login(LoginRequestDto request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken =
                jwtService.generateAccessToken(user.getId(), user.getRole());

        String refreshToken =
                refreshTokenService.generateRefreshToken(user.getId());

        return new LoginResponseDto(
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn
        );
    }

    @Transactional
    public LoginResponseDto refresh(String refreshToken) {

        RefreshTokenService.RotationResult result =
                refreshTokenService.rotateRefreshToken(refreshToken);

        String accessToken =
                jwtService.generateAccessToken(result.getUserId(), result.getRole());

        return new LoginResponseDto(
                accessToken,
                result.getRefreshToken(),
                "Bearer",
                expiresIn
        );
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }
}