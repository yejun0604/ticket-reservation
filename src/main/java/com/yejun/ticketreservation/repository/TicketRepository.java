package com.yejun.ticketreservation.repository;

import com.yejun.ticketreservation.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
