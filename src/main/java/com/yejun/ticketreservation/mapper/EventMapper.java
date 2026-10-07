package com.yejun.ticketreservation.mapper;

import com.yejun.ticketreservation.domain.Event;
import com.yejun.ticketreservation.dto.EventResponseDto;

public class EventMapper {

    public static EventResponseDto toDto(Event event){

        EventResponseDto eventDto = new EventResponseDto();

        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setVenue(event.getVenue());
        eventDto.setStartTime(event.getStartTime());
        eventDto.setStatus(event.getStatus());

        eventDto.setTickets(event.getTickets()
                .stream()
                .map(TicketMapper::toDto)
                .toList());

        return eventDto;

    }
}
