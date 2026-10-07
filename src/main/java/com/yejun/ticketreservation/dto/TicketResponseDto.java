package com.yejun.ticketreservation.dto;

import com.yejun.ticketreservation.domain.TicketType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketResponseDto {

    private Long id;

    private TicketType ticketType;

    private BigDecimal price;

    private int totalQuantity;

    private int remainingQuantity;
}
