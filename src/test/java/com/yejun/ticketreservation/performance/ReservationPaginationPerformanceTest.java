
package com.yejun.ticketreservation.performance;

import com.yejun.ticketreservation.domain.*;
import com.yejun.ticketreservation.dto.*;
import com.yejun.ticketreservation.repository.*;
import com.yejun.ticketreservation.service.ReservationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationPaginationPerformanceTest {

    @Autowired ReservationService reservationService;
    @Autowired EventRepository eventRepository;
    @Autowired TicketRepository ticketRepository;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager entityManager;
    @Autowired EntityManagerFactory entityManagerFactory;

    @Test
    @Transactional
    void reservationPaginationUsesBoundedSqlQueries() {

        User user = userRepository.save(User.createUser(
                "Pagination Test User",
                "pagination-" + UUID.randomUUID() + "@test.com"
        ));

        List<Long> expectedReservationIds = new ArrayList<>();

        for (int i = 0; i < 20; i++) {

            Event event = eventRepository.save(Event.createEvent(
                    "Pagination Test Event " + i,
                    "Test Venue",
                    LocalDateTime.now().plusDays(7)
            ));

            Ticket ticket = ticketRepository.save(Ticket.createTicket(
                    event,
                    TicketType.GENERAL,
                    BigDecimal.valueOf(100),
                    1
            ));

            ReservationItemRequestDto item =
                    new ReservationItemRequestDto();

            item.setTicketId(ticket.getId());
            item.setQuantity(1);

            ReservationCreateRequestDto request =
                    new ReservationCreateRequestDto();

            request.setUserId(user.getId());
            request.setEventId(event.getId());
            request.setItems(List.of(item));

            expectedReservationIds.add(
                    reservationService.createReservation(request)
                            .getReservationId()
            );
        }

        entityManager.flush();
        entityManager.clear();

        Statistics statistics = entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();

        statistics.setStatisticsEnabled(true);
        statistics.clear();

        Page<ReservationSummaryResponseDto> page =
                reservationService.getReservations(
                        PageRequest.of(
                                0,
                                20,
                                Sort.by("id").descending()
                        )
                );

        long queryCount = statistics.getPrepareStatementCount();

        assertThat(page.getNumberOfElements()).isEqualTo(20);

        assertThat(page.getTotalElements())
                .isGreaterThanOrEqualTo(20);

        assertThat(page.getContent())
                .extracting(ReservationSummaryResponseDto::getReservationId)
                .containsExactlyInAnyOrderElementsOf(expectedReservationIds);

        assertThat(queryCount).isLessThanOrEqualTo(5L);

        System.out.println(
                "Returned reservations: " + page.getNumberOfElements()
        );
        System.out.println(
                "Total reservations: " + page.getTotalElements()
        );
        System.out.println(
                "Executed SQL statements: " + queryCount
        );
    }
}
