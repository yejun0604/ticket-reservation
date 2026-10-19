package com.yejun.ticketreservation.dto;

import com.yejun.ticketreservation.domain.TicketType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReservationItemResponseDto {

    private Long id;

    private Long ticketId;

    private TicketType ticketType;

    private int quantity;

    private BigDecimal unitPrice;
}
