package com.yejun.ticketreservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @Size(max = 2, message = "An event can have at most 2 ticket types")
    private List<TicketCreateRequestDto> tickets;
}
