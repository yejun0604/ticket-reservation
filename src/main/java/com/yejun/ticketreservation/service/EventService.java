package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.domain.Event;
import com.yejun.ticketreservation.domain.Ticket;
import com.yejun.ticketreservation.dto.EventCreateRequestDto;
import com.yejun.ticketreservation.dto.EventResponseDto;
import com.yejun.ticketreservation.dto.EventUpdateRequestDto;
import com.yejun.ticketreservation.dto.TicketCreateRequestDto;
import com.yejun.ticketreservation.exception.EventNotFoundException;
import com.yejun.ticketreservation.mapper.EventMapper;
import com.yejun.ticketreservation.repository.EventRepository;
import com.yejun.ticketreservation.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;


    // To get one event, it sends 2 queries (One for event, one for Ticket)
    public EventResponseDto getEvent(Long eventId){

        Event event = eventRepository.findByIdWithTickets(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        return EventMapper.toDto(event);
    }

    // N + 1 problem, when fetching Tickets for each Event
    public List<EventResponseDto> getEvents() {

        List<Event> events = eventRepository.findAllWithTickets();

        return events
                .stream()
                .map(EventMapper::toDto)
                .toList();
    }

    @Transactional
    public EventResponseDto createEvent(EventCreateRequestDto eventCreateRequestDto){

        Event event = Event.createEvent(
                eventCreateRequestDto.getTitle(),
                eventCreateRequestDto.getVenue(),
                eventCreateRequestDto.getStartTime()
        );

        eventRepository.save(event);

        List<TicketCreateRequestDto> tickets = eventCreateRequestDto.getTickets();

        for(int i = 0; i < tickets.size(); i++){

            TicketCreateRequestDto ticketDto = tickets.get(i);

            Ticket ticket = Ticket.createTicket(
                                event,
                                ticketDto.getTicketType(),
                                ticketDto.getPrice(),
                                ticketDto.getTotalQuantity());

            event.addTicket(ticket);

            ticketRepository.save(ticket);
        }

        return EventMapper.toDto(event);

    }

    @Transactional
    public EventResponseDto updateEvent(
            Long eventId,
            EventUpdateRequestDto dto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        event.updateEvent(
                dto.getTitle(),
                dto.getVenue(),
                dto.getStartTime());

        return EventMapper.toDto(event);
    }

    @Transactional
    public void cancelEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        event.cancelEvent();

    }
}
