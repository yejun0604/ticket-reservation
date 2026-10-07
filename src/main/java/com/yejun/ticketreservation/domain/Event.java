package com.yejun.ticketreservation.domain;

import com.yejun.ticketreservation.dto.TicketCreateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter

public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @OneToMany(mappedBy = "event")
    private List<Ticket> tickets = new ArrayList<>();

    public static Event createEvent(String title,
                                    String venue,
                                    LocalDateTime startTime){

        Event event = new Event();

        event.title = title;
        event.venue = venue;
        event.startTime = startTime;
        event.status = EventStatus.OPEN;

        return event;
    }

    public void addTicket(Ticket ticket) {

        tickets.add(ticket);
    }

    public void updateEvent(
            String title,
            String venue,
            LocalDateTime startTime
    ) {
        this.title = title;
        this.venue = venue;
        this.startTime = startTime;
    }

    public void cancelEvent() {
        this.status = EventStatus.CANCELLED;
    }
}
