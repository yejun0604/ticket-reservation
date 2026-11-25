package com.yejun.ticketreservation.controller;

import com.yejun.ticketreservation.dto.ReservationCreateRequestDto;
import com.yejun.ticketreservation.dto.ReservationDetailResponseDto;
import com.yejun.ticketreservation.dto.ReservationSummaryResponseDto;
import com.yejun.ticketreservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponseDto> getReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok().body(
                reservationService.getReservation(userId, reservationId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<ReservationSummaryResponseDto>> getReservations(
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt
    ){

        Long userId = Long.valueOf(jwt.getSubject());

        Page<ReservationSummaryResponseDto> reservations =
                reservationService.getReservations(userId, pageable);

        return ResponseEntity.ok().body(reservations);
    }

    @PostMapping
    public ResponseEntity<ReservationDetailResponseDto> createReservation(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReservationCreateRequestDto request
    ){

        Long userId = Long.valueOf(jwt.getSubject());

        ReservationDetailResponseDto reservation =
                reservationService.createReservation(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = Long.valueOf(jwt.getSubject());

        reservationService.cancelReservation(userId, reservationId);

        return ResponseEntity.noContent().build();
    }

}