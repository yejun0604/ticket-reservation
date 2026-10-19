package com.yejun.ticketreservation.exception;

public class EventNotReservableException extends RuntimeException {
    public EventNotReservableException(String message) {
        super(message);
    }
}
