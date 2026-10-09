package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.dto.EventDetailResponseDto;
import com.yejun.ticketreservation.dto.EventSummaryResponseDto;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EventQueryPerformanceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {

        statistics = entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();

        statistics.clear();
    }

    @Test
    void getEvents_shouldNotCauseNPlusOne() {

        // 첫 페이지에서 Event 20개 조회
        Pageable pageable = PageRequest.of(0, 20);

        // 실행
        Page<EventSummaryResponseDto> events =
                eventService.getEvents(pageable);

        long queryCount = statistics.getPrepareStatementCount();

        System.out.println("getEvents query count = " + queryCount);

        assertThat(events.getContent()).isNotEmpty();

        // Event 목록 조회 1번
        // + Page 전체 개수를 위한 COUNT 쿼리가 실행될 수 있음
        //
        // Ticket에는 접근하지 않으므로 N+1은 발생하면 안 됨
        assertThat(queryCount).isBetween(1L, 2L);
    }

    @Test
    void getEvent_shouldExecuteOneQuery() {

        Long eventId = 10001L;

        EventDetailResponseDto event =
                eventService.getEvent(eventId);

        long queryCount = statistics.getPrepareStatementCount();

        System.out.println("getEvent query count = " + queryCount);

        assertThat(event).isNotNull();

        // Event + Tickets Fetch Join
        assertThat(queryCount).isEqualTo(1);
    }
}