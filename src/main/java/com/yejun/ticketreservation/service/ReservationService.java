package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.domain.*;
import com.yejun.ticketreservation.dto.ReservationCreateRequestDto;
import com.yejun.ticketreservation.dto.ReservationDetailResponseDto;
import com.yejun.ticketreservation.dto.ReservationItemRequestDto;
import com.yejun.ticketreservation.dto.ReservationSummaryResponseDto;
import com.yejun.ticketreservation.exception.*;
import com.yejun.ticketreservation.mapper.ReservationMapper;
import com.yejun.ticketreservation.repository.EventRepository;
import com.yejun.ticketreservation.repository.ReservationRepository;
import com.yejun.ticketreservation.repository.TicketRepository;
import com.yejun.ticketreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public ReservationDetailResponseDto getReservation(Long userId, Long reservationId){

        Reservation reservation = reservationRepository.findByIdWithDetails(reservationId, userId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + reservationId));

        return ReservationMapper.toDto(reservation);

    }

    public Page<ReservationSummaryResponseDto> getReservations(Long userId, Pageable pageable){

        return reservationRepository.findAllWithEvent(userId, pageable)
                .map(ReservationMapper::toSummaryDto);
    }

    @Transactional
    public ReservationDetailResponseDto createReservation(Long userId, ReservationCreateRequestDto request){

        User user = userRepository.findByIdForActiveOperation(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (!user.isActive()) {
            throw new AccessDeniedException("Account is inactive");
        }

        Event event = eventRepository.findByIdForReservation(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + request.getEventId()));

        LocalDateTime now = LocalDateTime.now();

        if (event.getStatus() == EventStatus.CANCELLED) {

            throw new EventNotReservableException("Cancelled event cannot be reserved");
        }

        if (!event.getStartTime().isAfter(now)) {

            throw new EventNotReservableException("Past event cannot be reserved");
        }

        Reservation reservation = Reservation.createReservation(user, event, now);

        Set<Long> requestedTicketIds = new HashSet<>();

        for(int i = 0; i < request.getItems().size(); i++){

            ReservationItemRequestDto itemRequestDto = request.getItems().get(i);

            Long ticketId = itemRequestDto.getTicketId();

            if (!requestedTicketIds.add(ticketId)) {

                throw new DuplicateReservationTicketException("Duplicate ticket in reservation: " + ticketId);
            }

            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + itemRequestDto.getTicketId()));

            if (!ticket.getEvent().getId().equals(event.getId())){

                throw new TicketEventMismatchException("Ticket does not belong to this event: " + ticket.getId());
            }

            ticket.decreaseQuantity(itemRequestDto.getQuantity());

            ReservationItem reservationItem = ReservationItem.createReservationItem(ticket, itemRequestDto.getQuantity());

            reservation.addReservationItem(reservationItem);

        }

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationMapper.toDto(savedReservation);

    }

    @Transactional
    public void cancelReservation(Long userId, Long reservationId){

        Long eventId = reservationRepository.findEventIdByReservationId(reservationId, userId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + reservationId));

        eventRepository.findByIdForReservation(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        Reservation reservation = reservationRepository.findByIdForCancel(reservationId, userId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + reservationId));

        reservation.cancelReservation();

        for (int i = 0; i < reservation.getReservationItems().size(); i++) {

            ReservationItem reservationItem = reservation.getReservationItems().get(i);

            int ticketQuantity = reservationItem.getQuantity();

            reservationItem.getTicket().increaseQuantity(ticketQuantity);
        }
    }


}