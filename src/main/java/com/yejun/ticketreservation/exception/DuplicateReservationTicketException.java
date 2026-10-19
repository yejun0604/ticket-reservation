package com.yejun.ticketreservation.exception;

public class DuplicateReservationTicketException extends RuntimeException {
    public DuplicateReservationTicketException(String message) {
        super(message);
    }
}
