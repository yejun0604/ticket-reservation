package com.yejun.ticketreservation.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class ReservationItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    public void assignReservation(Reservation reservation) {

        this.reservation = reservation;
    }


    public static ReservationItem createReservationItem(Ticket ticket, int quantity) {

        ReservationItem item = new ReservationItem();

        item.ticket = ticket;
        item.quantity = quantity;
        item.unitPrice = ticket.getPrice();

        return item;
    }

}
