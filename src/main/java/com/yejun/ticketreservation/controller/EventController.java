package com.yejun.ticketreservation.controller;

import com.yejun.ticketreservation.dto.EventCreateRequestDto;
import com.yejun.ticketreservation.dto.EventDetailResponseDto;
import com.yejun.ticketreservation.dto.EventSummaryResponseDto;
import com.yejun.ticketreservation.dto.EventUpdateRequestDto;
import com.yejun.ticketreservation.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailResponseDto> getEvent(@PathVariable("eventId") Long eventId) {

        return ResponseEntity.ok().body(eventService.getEvent(eventId));
    }

    @GetMapping
    public ResponseEntity<Page<EventSummaryResponseDto>> getEvents(Pageable pageable) {

        Page<EventSummaryResponseDto> events = eventService.getEvents(pageable);

        return ResponseEntity.ok().body(events);
    }

    @PostMapping
    public ResponseEntity<EventDetailResponseDto> createEvent(@Valid @RequestBody EventCreateRequestDto eventCreateRequestDto) {

        EventDetailResponseDto event = eventService.createEvent(eventCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDetailResponseDto> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventUpdateRequestDto dto
    ) {

        return ResponseEntity.ok(
                eventService.updateEvent(eventId, dto)
        );
    }

    @PatchMapping("/{eventId}/cancel")
    public ResponseEntity<Void> cancelEvent(@PathVariable("eventId") Long eventId){

        eventService.cancelEvent(eventId);

        return ResponseEntity.noContent().build();
    }


}
