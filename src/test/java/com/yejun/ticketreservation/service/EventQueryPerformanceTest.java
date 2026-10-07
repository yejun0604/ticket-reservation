package com.yejun.ticketreservation.service;

import com.yejun.ticketreservation.dto.EventResponseDto;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

        // Hibernate Statistics 가져오기
        statistics = entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();

        // 이전에 실행된 SQL 개수 기록 초기화
        statistics.clear();
    }

    @Test
    void getEvents_shouldExecuteOneQuery() {

        // 실행
        List<EventResponseDto> events = eventService.getEvents();

        // 실제 실행된 SQL 개수
        long queryCount = statistics.getPrepareStatementCount();

        System.out.println("getEvents query count = " + queryCount);

        // 데이터가 실제 조회됐는지
        assertThat(events).isNotEmpty();

        // Fetch Join 적용 후 SQL은 1번이어야 함
        assertThat(queryCount).isEqualTo(1);
    }

    @Test
    void getEvent_shouldExecuteOneQuery() {

        // 우리가 seed로 넣은 Event
        Long eventId = 10001L;

        // 실행
        EventResponseDto event = eventService.getEvent(eventId);

        // 실제 실행된 SQL 개수
        long queryCount = statistics.getPrepareStatementCount();

        System.out.println("getEvent query count = " + queryCount);

        assertThat(event).isNotNull();

        // Event + Tickets Fetch Join → SQL 1번
        assertThat(queryCount).isEqualTo(1);
    }
}
