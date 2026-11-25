package com.yejun.ticketreservation.security;

import com.yejun.ticketreservation.domain.Role;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class CustomUserDetails extends User {

    private final Long id;
    private final Role role;

    public CustomUserDetails(
            Long id,
            String email,
            String passwordHash,
            Role role,
            boolean active
    ) {
        super(
                email,
                passwordHash,
                active,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );

        this.id = id;
        this.role = role;
    }
}