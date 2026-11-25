package com.yejun.ticketreservation.security;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class CustomUserDetails extends User {

    private final Long id;

    public CustomUserDetails(Long id, String email, String passwordHash) {
        super(email, passwordHash, List.of());
        this.id = id;
    }
}