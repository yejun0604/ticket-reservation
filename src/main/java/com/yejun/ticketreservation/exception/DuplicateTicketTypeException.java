package com.yejun.ticketreservation.exception;

public class DuplicateTicketTypeException extends RuntimeException {
    public DuplicateTicketTypeException(String message) {
        super(message);
    }
}
