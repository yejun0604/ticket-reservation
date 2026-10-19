package com.yejun.ticketreservation.mapper;

import com.yejun.ticketreservation.domain.Ticket;
import com.yejun.ticketreservation.dto.TicketResponseDto;

public class TicketMapper {

    public static TicketResponseDto toDto(Ticket ticket){

        TicketResponseDto ticketResponseDto = new TicketResponseDto();

        ticketResponseDto.setId(ticket.getId());
        ticketResponseDto.setTicketType(ticket.getTicketType());
        ticketResponseDto.setPrice(ticket.getPrice());
        ticketResponseDto.setTotalQuantity(ticket.getTotalQuantity());
        ticketResponseDto.setRemainingQuantity(ticket.getRemainingQuantity());

        return ticketResponseDto;
    }

}
