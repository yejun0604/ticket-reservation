package com.yejun.ticketreservation.dto;

import com.yejun.ticketreservation.domain.TicketType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketCreateRequestDto {

    @NotNull(message = "Ticket type is required")
    private TicketType ticketType;

    @NotNull(message = "Price is required")
    @Positive
    private BigDecimal price;

    @PositiveOrZero(message = "Total quantity cannot be negative")
    private int totalQuantity;

}
