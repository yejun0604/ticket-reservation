package com.yejun.ticketreservation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReservationItemRequestDto {

    @NotNull(message = "Ticket id is required")
    private Long ticketId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
