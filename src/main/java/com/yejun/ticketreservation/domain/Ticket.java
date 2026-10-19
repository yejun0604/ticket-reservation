package com.yejun.ticketreservation.domain;
import com.yejun.ticketreservation.exception.InsufficientTicketStockException;
import com.yejun.ticketreservation.exception.InvalidTicketQuantityException;
import com.yejun.ticketreservation.exception.TicketStockConsistencyException;
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

    @Version
    private Long version;

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


    public static Ticket createTicket(Event event,
                                      TicketType ticketType,
                                      BigDecimal price,
                                      int totalQuantity) {

        Ticket ticket = new Ticket();

        ticket.event = event;
        ticket.ticketType = ticketType;
        ticket.price = price;
        ticket.totalQuantity = totalQuantity;
        ticket.remainingQuantity = totalQuantity;

        return ticket;
    }

    public void increaseQuantity(int quantity) {

        if (quantity <= 0) {

            throw new InvalidTicketQuantityException("Ticket quantity must be greater than 0");
        }

        if (quantity > totalQuantity - remainingQuantity) {

            throw new TicketStockConsistencyException("Ticket stock would exceed total quantity");
        }

        remainingQuantity += quantity;
    }

    public void decreaseQuantity(int quantity){

        if (quantity <= 0){

            throw new InvalidTicketQuantityException("Quantity must be greater than 0");
        }

        if(remainingQuantity < quantity){

            throw new InsufficientTicketStockException("Not enough ticket stock");
        }

        remainingQuantity -= quantity;
    }
}