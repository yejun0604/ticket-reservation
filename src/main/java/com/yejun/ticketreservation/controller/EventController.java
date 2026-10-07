package com.yejun.ticketreservation.controller;

import com.yejun.ticketreservation.dto.EventCreateRequestDto;
import com.yejun.ticketreservation.dto.EventResponseDto;
import com.yejun.ticketreservation.dto.EventUpdateRequestDto;
import com.yejun.ticketreservation.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    //이거 나중에 service에서 쿼리2개 안나가게 고친다음 테스트 다시.
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEvent( @PathVariable("eventId") Long eventId) {

        return ResponseEntity.ok().body(eventService.getEvent(eventId));
    }

    //이거 나중에 service에서 쿼리 N+1 안나가게 고친다음 테스트 다시.
    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getEvents() {

        List<EventResponseDto> events = eventService.getEvents();

        return ResponseEntity.ok().body(events);
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventCreateRequestDto eventCreateRequestDto) {

        EventResponseDto event = eventService.createEvent(eventCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(
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
