package com.yejun.ticketreservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDto {

    @NotBlank(message = "Refresh token is required")
    @Size(min = 43, max = 43, message = "Invalid refresh token length")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String refreshToken;
}