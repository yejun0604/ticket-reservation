package com.yejun.ticketreservation.dto;

import com.yejun.ticketreservation.domain.ReservationStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReservationSummaryResponseDto {

    private Long reservationId;
    private ReservationStatus status;
    private BigDecimal totalPrice;

    private Long eventId;
    private String eventTitle;
    private String eventVenue;
    private LocalDateTime eventStartTime;

    private List<ReservationItemSummaryResponseDto> items = new ArrayList<>();
}