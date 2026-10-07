package com.yejun.ticketreservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventUpdateRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
}
