package com.yejun.ticketreservation.mapper;

import com.yejun.ticketreservation.domain.Ticket;
import com.yejun.ticketreservation.dto.TicketResponseDto;

public class TicketMapper {

    public static TicketResponseDto toDto(Ticket ticket){

        TicketResponseDto ticketDto = new TicketResponseDto();

        ticketDto.setId(ticket.getId());
        ticketDto.setTicketType(ticket.getTicketType());
        ticketDto.setPrice(ticket.getPrice());
        ticketDto.setTotalQuantity(ticket.getTotalQuantity());
        ticketDto.setRemainingQuantity(ticket.getRemainingQuantity());

        return ticketDto;
    }

}
