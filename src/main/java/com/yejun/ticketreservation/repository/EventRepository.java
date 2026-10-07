package com.yejun.ticketreservation.repository;

import com.yejun.ticketreservation.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

        @Query("""
            select e
            from Event e
            left join fetch e.tickets
            where e.id = :eventId
        """)
        Optional<Event> findByIdWithTickets(
                @Param("eventId") Long eventId
        );

        @Query("""
        select e
        from Event e
        left join fetch e.tickets
        """)
        List<Event> findAllWithTickets();
}
