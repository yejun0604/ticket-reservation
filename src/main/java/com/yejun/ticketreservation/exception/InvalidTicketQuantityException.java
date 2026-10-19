package com.yejun.ticketreservation.exception;

public class InvalidTicketQuantityException extends RuntimeException {
    public InvalidTicketQuantityException(String message) {
        super(message);
    }
}
