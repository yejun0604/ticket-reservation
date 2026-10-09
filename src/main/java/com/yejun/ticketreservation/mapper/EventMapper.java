package com.yejun.ticketreservation.mapper;

import com.yejun.ticketreservation.domain.Event;
import com.yejun.ticketreservation.dto.EventDetailResponseDto;
import com.yejun.ticketreservation.dto.EventSummaryResponseDto;

public class EventMapper {

    public static EventDetailResponseDto toDto(Event event){

        EventDetailResponseDto eventDto = new EventDetailResponseDto();

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

    public static EventSummaryResponseDto toSummaryDto(Event event) {

        EventSummaryResponseDto dto = new EventSummaryResponseDto();

        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setVenue(event.getVenue());
        dto.setStartTime(event.getStartTime());
        dto.setStatus(event.getStatus());

        return dto;
    }
}
