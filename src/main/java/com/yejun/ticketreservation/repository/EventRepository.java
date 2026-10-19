package com.yejun.ticketreservation.repository;

import com.yejun.ticketreservation.domain.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

        @Lock(LockModeType.PESSIMISTIC_READ)
        @Query("""
            select e
            from Event e
            where e.id = :eventId
        """) Optional<Event> findByIdForReservation(
                @Param("eventId") Long eventId
        );

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
            select e
            from Event e
            where e.id = :eventId
        """)
        Optional<Event> findByIdForCancellation(
                @Param("eventId") Long eventId
        );

}
