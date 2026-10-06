package com.yejun.ticketreservation.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ticket_event_type",
                columnNames = {"event_id", "ticket_type"}
        )
)
public class Ticket {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type", nullable = false)
    private TicketType ticketType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int remainingQuantity;
}