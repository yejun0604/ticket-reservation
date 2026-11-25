package com.yejun.ticketreservation.repository;

import com.yejun.ticketreservation.domain.Reservation;
import com.yejun.ticketreservation.domain.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndStatus(Long userId, ReservationStatus status);

    @Query("""
        select r
        from Reservation r
        join fetch r.user
        join fetch r.event
        join fetch r.reservationItems ri
        join fetch ri.ticket
        where r.id = :reservationId
        and r.user.id = :userId
        """)
    Optional<Reservation> findByIdWithDetails(
            @Param("reservationId") Long reservationId,
            @Param("userId") Long userId
    );

    @Query(value = """
        select r
        from Reservation r
        join fetch r.event
        where r.user.id = :userId
        """, countQuery = """
        select count(r)
        from Reservation r
        where r.user.id = :userId
        """)
    Page<Reservation> findAllWithEvent(
            @Param("userId") Long userId,
            Pageable pageable
    );


    @Query("""
        select r
        from Reservation r
        join fetch r.reservationItems ri
        join fetch ri.ticket
        where r.id = :reservationId
        and r.user.id = :userId
        """)
    Optional<Reservation> findByIdForCancel(
            @Param("reservationId") Long reservationId,
            @Param("userId") Long userId
    );

    @Query("""
        select distinct r
        from Reservation r
        join fetch r.reservationItems ri
        join fetch ri.ticket
        where r.event.id = :eventId
        and r.status = :status
        """)
    List<Reservation> findByEventIdAndStatusForCancel(
            @Param("eventId") Long eventId,
            @Param("status") ReservationStatus status
    );

    @Query("""
    select r.event.id
    from Reservation r
    where r.id = :reservationId
    and r.user.id = :userId
    """)
    Optional<Long> findEventIdByReservationId(
            @Param("reservationId") Long reservationId,
            @Param("userId") Long userId
    );
}