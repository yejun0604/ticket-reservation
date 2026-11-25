package com.yejun.ticketreservation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import com.yejun.ticketreservation.domain.Role;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-token-expiration}")
    private long expiresIn;

    public String generateAccessToken(Long userId, Role role) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .claim("role", role.name())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}