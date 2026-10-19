package com.yejun.ticketreservation.exception;

public class InsufficientTicketStockException extends RuntimeException {
    public InsufficientTicketStockException(String message) {
        super(message);
    }
}
