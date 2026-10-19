package com.yejun.ticketreservation.exception;

public class ReservationAlreadyCancelledException extends RuntimeException {
    public ReservationAlreadyCancelledException(String message) {
        super(message);
    }
}
