package com.yejun.ticketreservation.concurrency;

import com.yejun.ticketreservation.domain.*;
import com.yejun.ticketreservation.dto.*;
import com.yejun.ticketreservation.exception.EventNotReservableException;
import com.yejun.ticketreservation.exception.ReservationAlreadyCancelledException;
import com.yejun.ticketreservation.repository.*;
import com.yejun.ticketreservation.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class EventCancellationLockTest {

    @Autowired EventRepository eventRepository;
    @Autowired TicketRepository ticketRepository;
    @Autowired UserRepository userRepository;
    @Autowired ReservationRepository reservationRepository;
    @Autowired ReservationService reservationService;
    @Autowired EventService eventService;
    @Autowired PlatformTransactionManager transactionManager;

    private Event event;
    private Ticket ticket;
    private User user;
    private ReservationCreateRequestDto request;

    @BeforeEach
    void setUp() {

        event = eventRepository.save(
                Event.createEvent(
                        "Lock Test Event",
                        "Test Venue",
                        LocalDateTime.now().plusDays(7)
                )
        );

        ticket = ticketRepository.save(
                Ticket.createTicket(
                        event,
                        TicketType.GENERAL,
                        BigDecimal.valueOf(100),
                        1
                )
        );

        user = userRepository.save(
                User.createUser(
                        "Test User",
                        "locktest" + UUID.randomUUID() + "@test.com"
                )
        );

        ReservationItemRequestDto item =
                new ReservationItemRequestDto();

        item.setTicketId(ticket.getId());
        item.setQuantity(1);

        request = new ReservationCreateRequestDto();
        request.setUserId(user.getId());
        request.setEventId(event.getId());
        request.setItems(List.of(item));
    }

    @Test
    void cancellationBlocksNewReservation() throws Exception {

        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch allowCommit = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        TransactionTemplate transaction = new TransactionTemplate(transactionManager);

        try {

            Future<?> cancellation = executor.submit(() -> transaction.executeWithoutResult(status -> {

                        eventRepository.findByIdForCancellation(event.getId()).orElseThrow();

                        locked.countDown();

                        try {
                            if (!allowCommit.await(10, TimeUnit.SECONDS)) {
                                throw new IllegalStateException("Latch timeout");
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException(e);
                        }

                        eventService.cancelEvent(event.getId());
                    })
            );

            assertThat(locked.await(5, TimeUnit.SECONDS)).isTrue();

            CountDownLatch started = new CountDownLatch(1);

            Future<?> reservation = executor.submit(() -> {
                started.countDown();
                return reservationService.createReservation(request);
            });

            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

            assertThrows(
                    TimeoutException.class,
                    () -> reservation.get(500, TimeUnit.MILLISECONDS)
            );

            allowCommit.countDown();
            cancellation.get(10, TimeUnit.SECONDS);

            ExecutionException failure = assertThrows(
                    ExecutionException.class,
                    () -> reservation.get(10, TimeUnit.SECONDS)
            );

            assertThat(failure.getCause())
                    .isInstanceOf(EventNotReservableException.class);

            Event updatedEvent = eventRepository.findById(event.getId())
                    .orElseThrow();

            Ticket updatedTicket = ticketRepository.findById(ticket.getId())
                    .orElseThrow();

            assertThat(updatedEvent.getStatus())
                    .isEqualTo(EventStatus.CANCELLED);

            assertThat(updatedTicket.getRemainingQuantity())
                    .isEqualTo(1);

            assertThat(reservationRepository.existsByUserId(user.getId()))
                    .isFalse();

        } finally {
            allowCommit.countDown();
            executor.shutdownNow();
        }
    }


    @Test
    void reservationFirstThenCancellationRestoresStock() throws Exception {

        CountDownLatch readLocked = new CountDownLatch(1);
        CountDownLatch allowReservation = new CountDownLatch(1);
        CountDownLatch cancellationStarted = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<Long> booking = executor.submit(() ->
                    new TransactionTemplate(transactionManager)
                            .execute(status -> {

                                eventRepository.findByIdForReservation(event.getId())
                                        .orElseThrow();

                                readLocked.countDown();

                                try {
                                    if (!allowReservation.await(10, TimeUnit.SECONDS)) {
                                        throw new IllegalStateException("Timeout");
                                    }
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    throw new IllegalStateException(e);
                                }

                                return reservationService.createReservation(request)
                                        .getReservationId();
                            })
            );

            assertThat(readLocked.await(5, TimeUnit.SECONDS)).isTrue();

            Future<?> cancellation = executor.submit(() -> {
                cancellationStarted.countDown();
                eventService.cancelEvent(event.getId());
            });

            assertThat(cancellationStarted.await(5, TimeUnit.SECONDS)).isTrue();

            assertThrows(
                    TimeoutException.class,
                    () -> cancellation.get(500, TimeUnit.MILLISECONDS)
            );

            allowReservation.countDown();

            Long reservationId = booking.get(10, TimeUnit.SECONDS);
            cancellation.get(10, TimeUnit.SECONDS);

            assertThat(eventRepository.findById(event.getId())
                    .orElseThrow().getStatus())
                    .isEqualTo(EventStatus.CANCELLED);

            assertThat(reservationRepository.findById(reservationId)
                    .orElseThrow().getStatus())
                    .isEqualTo(ReservationStatus.CANCELLED);

            assertThat(ticketRepository.findById(ticket.getId())
                    .orElseThrow().getRemainingQuantity())
                    .isEqualTo(1);

        } finally {
            allowReservation.countDown();
            executor.shutdownNow();
        }
    }


    private static void awaitOrFail(CountDownLatch latch) {
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Latch timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }


    // TEST 3: 관리자가 먼저 Event를 취소
    @Test
    void eventCancellationFirstBlocksIndividualCancellation() throws Exception {

        // 먼저 예약을 하나 실제로 생성
        Long reservationId = reservationService
                .createReservation(request)
                .getReservationId();

        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch allowCommit = new CountDownLatch(1);
        CountDownLatch started = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        TransactionTemplate transaction =
                new TransactionTemplate(transactionManager);

        try {
            // 관리자: Event WRITE Lock 획득 후 대기
            Future<?> eventCancellation = executor.submit(() ->
                    transaction.executeWithoutResult(status -> {

                        eventRepository.findByIdForCancellation(event.getId())
                                .orElseThrow();

                        locked.countDown();

                        awaitOrFail(allowCommit);

                        eventService.cancelEvent(event.getId());
                    })
            );

            // 관리자가 WRITE Lock을 획득할 때까지 기다림
            assertThat(locked.await(5, TimeUnit.SECONDS)).isTrue();

            // 사용자: 동일한 예약을 취소하려고 시도
            Future<?> individualCancellation = executor.submit(() -> {

                started.countDown();

                reservationService.cancelReservation(reservationId);
            });

            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

            // 관리자 WRITE Lock이 유지되는 동안 사용자 취소는 미완료 상태여야 함
            assertThrows(
                    TimeoutException.class,
                    () -> individualCancellation.get(500, TimeUnit.MILLISECONDS)
            );

            // 관리자 Event 취소 진행 및 Commit
            allowCommit.countDown();

            eventCancellation.get(10, TimeUnit.SECONDS);

            // 사용자 개별 취소는 이미 취소된 예약이므로 실패
            ExecutionException failure = assertThrows(
                    ExecutionException.class,
                    () -> individualCancellation.get(10, TimeUnit.SECONDS)
            );

            assertThat(failure.getCause())
                    .isInstanceOf(ReservationAlreadyCancelledException.class);

            // 최종 Event 상태
            assertThat(eventRepository.findById(event.getId())
                    .orElseThrow().getStatus())
                    .isEqualTo(EventStatus.CANCELLED);

            // 최종 Reservation 상태
            assertThat(reservationRepository.findById(reservationId)
                    .orElseThrow().getStatus())
                    .isEqualTo(ReservationStatus.CANCELLED);

            // Ticket 재고는 원래 수량 1개로 복원돼야 함
            assertThat(ticketRepository.findById(ticket.getId())
                    .orElseThrow().getRemainingQuantity())
                    .isEqualTo(1);

        } finally {
            allowCommit.countDown();
            executor.shutdownNow();
        }
    }


    // TEST 4: 사용자가 먼저 Reservation을 취소
    @Test
    void individualCancellationFirstRestoresStockOnlyOnce() throws Exception {

        // 먼저 예약을 하나 실제로 생성
        Long reservationId = reservationService
                .createReservation(request)
                .getReservationId();

        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch allowCancellation = new CountDownLatch(1);
        CountDownLatch started = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        TransactionTemplate transaction =
                new TransactionTemplate(transactionManager);

        try {
            // 사용자: Event READ Lock 획득 후 대기
            Future<?> individualCancellation = executor.submit(() ->
                    transaction.executeWithoutResult(status -> {

                        eventRepository.findByIdForReservation(event.getId())
                                .orElseThrow();

                        locked.countDown();

                        awaitOrFail(allowCancellation);

                        reservationService.cancelReservation(reservationId);
                    })
            );

            // 사용자가 READ Lock을 획득할 때까지 기다림
            assertThat(locked.await(5, TimeUnit.SECONDS)).isTrue();

            // 관리자: Event 전체 취소 시도
            Future<?> eventCancellation = executor.submit(() -> {

                started.countDown();

                eventService.cancelEvent(event.getId());
            });

            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

            // 사용자 READ Lock 때문에 관리자 취소는 미완료 상태여야 함
            assertThrows(
                    TimeoutException.class,
                    () -> eventCancellation.get(500, TimeUnit.MILLISECONDS)
            );

            // 사용자 예약 취소 진행 및 Commit
            allowCancellation.countDown();

            individualCancellation.get(10, TimeUnit.SECONDS);

            // 관리자 Event 취소 완료까지 기다림
            eventCancellation.get(10, TimeUnit.SECONDS);

            // 최종 Event 상태
            assertThat(eventRepository.findById(event.getId())
                    .orElseThrow().getStatus())
                    .isEqualTo(EventStatus.CANCELLED);

            // 최종 Reservation 상태
            assertThat(reservationRepository.findById(reservationId)
                    .orElseThrow().getStatus())
                    .isEqualTo(ReservationStatus.CANCELLED);

            // Ticket 재고가 중복 복원되지 않았는지 확인
            assertThat(ticketRepository.findById(ticket.getId())
                    .orElseThrow().getRemainingQuantity())
                    .isEqualTo(1);

        } finally {
            allowCancellation.countDown();
            executor.shutdownNow();
        }
    }


}