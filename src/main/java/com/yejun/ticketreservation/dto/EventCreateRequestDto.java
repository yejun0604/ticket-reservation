package com.yejun.ticketreservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventCreateRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Venue is required")
    private String venue;

    @Future
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @Valid
    @NotEmpty(message = "Tickets is required")
    private List<TicketCreateRequestDto> tickets;
}
