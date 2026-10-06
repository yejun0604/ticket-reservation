package com.yejun.ticketreservation.repository;

import com.yejun.ticketreservation.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {


}
