package com.yejun.ticketreservation.dto;

import com.yejun.ticketreservation.domain.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventSummaryResponseDto {

    private Long id;

    private String title;

    private String venue;

    private LocalDateTime startTime;

    private EventStatus status;

}
