package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.repository.EventRepository;
import com.yejun.ticketreservation.repository.ReservationRepository;
import com.yejun.ticketreservation.repository.TicketRepository;
import com.yejun.ticketreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;


}
