package com.yejun.ticketreservation.concurrency;

import com.yejun.ticketreservation.domain.Event;
import com.yejun.ticketreservation.domain.Ticket;
import com.yejun.ticketreservation.domain.TicketType;
import com.yejun.ticketreservation.domain.User;
import com.yejun.ticketreservation.dto.ReservationCreateRequestDto;
import com.yejun.ticketreservation.dto.ReservationItemRequestDto;
import com.yejun.ticketreservation.exception.TicketNotFoundException;
import com.yejun.ticketreservation.repository.EventRepository;
import com.yejun.ticketreservation.repository.ReservationRepository;
import com.yejun.ticketreservation.repository.TicketRepository;
import com.yejun.ticketreservation.repository.UserRepository;
import com.yejun.ticketreservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    private Long userAId;
    private Long userBId;
    private Long eventId;
    private Long ticketId;

    @BeforeEach
    void setUp(){

        transactionTemplate = new TransactionTemplate(transactionManager);

        String unique = String.valueOf(System.nanoTime());

        User userA = User.createUser("User A", "usera" + unique + "@test.com");
        User userB = User.createUser("User B", "userb" + unique + "@test.com");

        userA = userRepository.save(userA);
        userB = userRepository.save(userB);

        userAId = userA.getId();
        userBId = userB.getId();

        Event event = Event.createEvent("Concurrency Test", " Test Venue", LocalDateTime.now().plusDays(1));

        event = eventRepository.save(event);

        eventId = event.getId();


        Ticket ticket = Ticket.createTicket(event, TicketType.GENERAL, BigDecimal.valueOf(100), 1);

        ticket = ticketRepository.save(ticket);

        ticketId = ticket.getId();

    }

    @Test
    void reservationOverSellingTest() throws Exception {

        ReservationItemRequestDto itemA = new ReservationItemRequestDto();
        itemA.setTicketId(ticketId);
        itemA.setQuantity(1);

        ReservationCreateRequestDto reservationA = new ReservationCreateRequestDto();
        reservationA.setUserId(userAId);
        reservationA.setEventId(eventId);
        reservationA.setItems(List.of(itemA));

        ReservationItemRequestDto itemB = new ReservationItemRequestDto();
        itemB.setTicketId(ticketId);
        itemB.setQuantity(1);

        ReservationCreateRequestDto reservationB = new ReservationCreateRequestDto();
        reservationB.setUserId(userBId);
        reservationB.setEventId(eventId);
        reservationB.setItems(List.of(itemB));

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch latch = new CountDownLatch(2);

        AtomicInteger successCount = new AtomicInteger(0);

        Future<?> a = executor.submit(() -> {
            createReservation(reservationA, latch);
            successCount.incrementAndGet();
        });

        Future<?> b = executor.submit(() -> {
            createReservation(reservationB, latch);
            successCount.incrementAndGet();
        });

        int conflictCount = 0;

        try {
            a.get();
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(ObjectOptimisticLockingFailureException.class);
            conflictCount++;
        }

        try {
            b.get();
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(ObjectOptimisticLockingFailureException.class);
            conflictCount++;
        }

        executor.shutdown();

        Ticket updatedTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + ticketId));

        int savedReservationCount = 0;

        if (reservationRepository.existsByUserId(userAId)) {
            savedReservationCount++;
        }

        if (reservationRepository.existsByUserId(userBId)) {
            savedReservationCount++;
        }

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(conflictCount).isEqualTo(1);
        assertThat(updatedTicket.getRemainingQuantity()).isEqualTo(0);
        assertThat(updatedTicket.getVersion()).isEqualTo(1L);
        assertThat(savedReservationCount).isEqualTo(1);

    }

    private void createReservation(ReservationCreateRequestDto request, CountDownLatch latch){

        transactionTemplate.executeWithoutResult(status -> {

            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + ticketId));

            latch.countDown();

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            reservationService.createReservation(request);
        });
    }

}
