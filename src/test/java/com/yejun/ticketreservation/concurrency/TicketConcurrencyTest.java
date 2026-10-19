
package com.yejun.ticketreservation.concurrency;

import com.yejun.ticketreservation.domain.Event;
import com.yejun.ticketreservation.domain.Ticket;
import com.yejun.ticketreservation.domain.TicketType;
import com.yejun.ticketreservation.exception.TicketNotFoundException;
import com.yejun.ticketreservation.repository.EventRepository;
import com.yejun.ticketreservation.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TicketConcurrencyTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EventRepository eventRepository;

    private TransactionTemplate transactionTemplate;

    private Long ticketId;

    @BeforeEach
    void setUp() {

        transactionTemplate =
                new TransactionTemplate(transactionManager);

        Event event = Event.createEvent(
                "Concurrency Test",
                "Test Venue",
                LocalDateTime.now().plusDays(1)
        );

        event = eventRepository.save(event);

        Ticket ticket = Ticket.createTicket(
                event,
                TicketType.GENERAL,
                BigDecimal.valueOf(100),
                1
        );

        ticket = ticketRepository.save(ticket);

        ticketId = ticket.getId();
    }

    @Test
    void overSellingTest() throws Exception {

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch latch = new CountDownLatch(2);

        AtomicInteger successCount = new AtomicInteger(0);

        Future<?> a = executor.submit(() -> {
            decreaseStock(ticketId, latch);
            successCount.incrementAndGet();
        });

        Future<?> b = executor.submit(() -> {
            decreaseStock(ticketId, latch);
            successCount.incrementAndGet();
        });

        int conflictCount = 0;

        try {
            a.get(15, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            assertThat(e.getCause())
                    .isInstanceOf(
                            ObjectOptimisticLockingFailureException.class
                    );
            conflictCount++;
        }

        try {
            b.get(15, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            assertThat(e.getCause())
                    .isInstanceOf(
                            ObjectOptimisticLockingFailureException.class
                    );
            conflictCount++;
        }

        executor.shutdown();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(
                        "Ticket not found: " + ticketId
                ));

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(conflictCount).isEqualTo(1);
        assertThat(ticket.getRemainingQuantity()).isEqualTo(0);
        assertThat(ticket.getVersion()).isEqualTo(1L);
    }

    private void decreaseStock(
            Long ticketId,
            CountDownLatch latch
    ) {

        transactionTemplate.executeWithoutResult(status -> {

            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException(
                            "Ticket not found: " + ticketId
                    ));

            latch.countDown();

            try {
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException(
                            "Both competing requests did not reach the barrier"
                    );
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }

            ticket.decreaseQuantity(1);
        });
    }
}
