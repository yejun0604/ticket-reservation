package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.domain.RefreshToken;
import com.yejun.ticketreservation.domain.User;
import com.yejun.ticketreservation.repository.RefreshTokenRepository;
import com.yejun.ticketreservation.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${app.refresh-token.expiration}")
    private long expiresIn;

    @Transactional
    public String generateRefreshToken(Long userId) {
        User user = userRepository.getReferenceById(userId);

        return issueToken(user);
    }

    private String issueToken(User user) {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken =
                RefreshToken.createRefreshToken(
                        user, tokenHash, expiresIn
                );

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm unavailable", e
            );
        }
    }

    @Transactional
    public RotationResult rotateRefreshToken(String rawToken) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        refreshToken.revoke();

        User user = refreshToken.getUser();

        String newToken = issueToken(user);

        return new RotationResult(user.getId(), newToken);
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {
        refreshTokenRepository.findByTokenHash(hashToken(rawToken))
                .ifPresent(RefreshToken::revoke);
    }

    @Getter
    @AllArgsConstructor
    public static class RotationResult {
        private final Long userId;
        private final String refreshToken;
    }
}