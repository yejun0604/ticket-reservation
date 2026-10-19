package com.yejun.ticketreservation.dto;

import com.yejun.ticketreservation.domain.ReservationStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReservationDetailResponseDto {

    private Long reservationId;
    private ReservationStatus status;
    private LocalDateTime reservedTime;
    private BigDecimal totalPrice;

    private Long userId;
    private String userName;
    private String userEmail;

    private Long eventId;
    private String eventTitle;
    private String eventVenue;
    private LocalDateTime eventStartTime;

    private List<ReservationItemResponseDto> items = new ArrayList<>();

}
