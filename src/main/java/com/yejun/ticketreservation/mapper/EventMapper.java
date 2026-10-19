package com.yejun.ticketreservation.mapper;

import com.yejun.ticketreservation.domain.Event;
import com.yejun.ticketreservation.dto.EventDetailResponseDto;
import com.yejun.ticketreservation.dto.EventSummaryResponseDto;

public class EventMapper {

    public static EventDetailResponseDto toDto(Event event){

        EventDetailResponseDto eventDetailDto = new EventDetailResponseDto();

        eventDetailDto.setId(event.getId());
        eventDetailDto.setTitle(event.getTitle());
        eventDetailDto.setVenue(event.getVenue());
        eventDetailDto.setStartTime(event.getStartTime());
        eventDetailDto.setStatus(event.getStatus());

        eventDetailDto.setTickets(event.getTickets()
                .stream()
                .map(TicketMapper::toDto)
                .toList());

        return eventDetailDto;

    }

    public static EventSummaryResponseDto toSummaryDto(Event event) {

        EventSummaryResponseDto eventSummaryDto = new EventSummaryResponseDto();

        eventSummaryDto.setId(event.getId());
        eventSummaryDto.setTitle(event.getTitle());
        eventSummaryDto.setVenue(event.getVenue());
        eventSummaryDto.setStartTime(event.getStartTime());
        eventSummaryDto.setStatus(event.getStatus());

        return eventSummaryDto;
    }
}
