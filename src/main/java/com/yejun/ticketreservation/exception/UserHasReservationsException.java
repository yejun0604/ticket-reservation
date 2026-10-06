package com.yejun.ticketreservation.exception;

public class UserHasReservationsException extends RuntimeException {
    public UserHasReservationsException(String message) {
        super(message);
    }
}
