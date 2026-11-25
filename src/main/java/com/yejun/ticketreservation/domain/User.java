package com.yejun.ticketreservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    public static User createUser(String name, String email, String passwordHash) {

        User user = new User();
        user.name = name;
        user.email = email;
        user.passwordHash = passwordHash;

        return user;
    }

    public void deactivate() {
        this.active = false;
    }

}
