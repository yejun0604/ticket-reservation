package com.yejun.ticketreservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequestDto {

    @NotEmpty(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    private String name;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;
}
