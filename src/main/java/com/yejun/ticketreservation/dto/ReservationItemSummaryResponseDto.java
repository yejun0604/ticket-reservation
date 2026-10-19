package com.yejun.ticketreservation.dto;

import com.yejun.ticketreservation.domain.TicketType;
import lombok.Data;

@Data
public class ReservationItemSummaryResponseDto {

    private TicketType ticketType;
    private int quantity;
}