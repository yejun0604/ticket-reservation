package com.yejun.ticketreservation.repository;

import com.yejun.ticketreservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUserId(Long userId);
}
