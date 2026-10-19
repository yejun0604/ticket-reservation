package com.yejun.ticketreservation.exception;

public class EventAlreadyCancelledException extends RuntimeException {
    public EventAlreadyCancelledException(String message) {
        super(message);
    }
}
