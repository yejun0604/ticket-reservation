package com.yejun.ticketreservation.domain;

import com.yejun.ticketreservation.exception.ReservationAlreadyCancelledException;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime reservedTime;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @OneToMany(
            mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ReservationItem> reservationItems = new ArrayList<>();

    public static Reservation createReservation(User user, Event event, LocalDateTime reservedTime){

        Reservation reservation = new Reservation();

        reservation.user = user;
        reservation.event = event;
        reservation.status = ReservationStatus.CONFIRMED;
        reservation.reservedTime = reservedTime;
        reservation.totalPrice = BigDecimal.ZERO;

        return reservation;
    }

    public void addReservationItem(ReservationItem item){

        reservationItems.add(item);

        item.assignReservation(this);

        BigDecimal itemPrice = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        totalPrice = totalPrice.add(itemPrice);
    }

    public void cancelReservation(){

        if (status == ReservationStatus.CANCELLED){

            throw new ReservationAlreadyCancelledException("Reservation is already cancelled: " + id);
        }

        status = ReservationStatus.CANCELLED;
    }
}
