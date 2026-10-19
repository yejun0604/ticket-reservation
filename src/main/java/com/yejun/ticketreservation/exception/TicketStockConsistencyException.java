package com.yejun.ticketreservation.exception;

public class TicketStockConsistencyException extends RuntimeException {
    public TicketStockConsistencyException(String message) {
        super(message);
    }
}
