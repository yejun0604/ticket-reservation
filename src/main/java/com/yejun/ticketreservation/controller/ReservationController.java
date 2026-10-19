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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponseDto> getReservation(@PathVariable("reservationId") Long reservationId) {

        return ResponseEntity.ok().body(reservationService.getReservation(reservationId));
    }

    @GetMapping
    public ResponseEntity<Page<ReservationSummaryResponseDto>> getReservations(Pageable pageable){

        Page<ReservationSummaryResponseDto> reservations = reservationService.getReservations(pageable);

        return ResponseEntity.ok().body(reservations);
    }

    @PostMapping
    public ResponseEntity<ReservationDetailResponseDto> createReservation(@Valid @RequestBody ReservationCreateRequestDto request){

        ReservationDetailResponseDto reservation = reservationService.createReservation(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable("reservationId") Long reservationId) {

        reservationService.cancelReservation(reservationId);

        return ResponseEntity.noContent().build();
    }

}
