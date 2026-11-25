package com.yejun.ticketreservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @ToString.Exclude
    private String password;

    @AssertTrue(message = "Password must not exceed 72 bytes")
    public boolean isPasswordLengthValid() {
        return password == null ||
                password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}