package com.yejun.ticketreservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }



    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(UserHasReservationsException.class)
    public ResponseEntity<Map<String, String>> handleUserHasReservationsException(UserHasReservationsException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEventNotFoundException(EventNotFoundException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTicketNotFoundException(TicketNotFoundException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleReservationNotFoundException(ReservationNotFoundException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ReservationAlreadyCancelledException.class)
    public ResponseEntity<Map<String, String>> handleReservationAlreadyCancelledException(ReservationAlreadyCancelledException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidTicketQuantityException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTicketQuantityException(InvalidTicketQuantityException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InsufficientTicketStockException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientTicketStockException(InsufficientTicketStockException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TicketStockConsistencyException.class)
    public ResponseEntity<Map<String, String>> handleTicketStockConsistencyException(TicketStockConsistencyException exception
    ){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(EventNotReservableException.class)
    public ResponseEntity<Map<String, String>> handleEventNotReservableException(EventNotReservableException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TicketEventMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTicketEventMismatchException(TicketEventMismatchException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DuplicateReservationTicketException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateReservationTicketException(DuplicateReservationTicketException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DuplicateTicketTypeException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateTicketTypeException(DuplicateTicketTypeException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EventAlreadyCancelledException.class)
    public ResponseEntity<Map<String, String>> handleEventAlreadyCancelledException(EventAlreadyCancelledException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, String>> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException exception){

        Map<String, String> error = new HashMap<>();
        error.put("message", "Ticket availability changed. Please try again.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}



