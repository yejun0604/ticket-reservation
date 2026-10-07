package com.yejun.ticketreservation.dto;

import com.yejun.ticketreservation.domain.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventResponseDto {

    private Long id;

    private String title;

    private String venue;

    private LocalDateTime startTime;

    private EventStatus status;

    private List<TicketResponseDto> tickets = new ArrayList<>();
}
