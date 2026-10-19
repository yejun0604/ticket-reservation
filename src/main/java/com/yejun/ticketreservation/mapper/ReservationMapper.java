package com.yejun.ticketreservation.mapper;

import com.yejun.ticketreservation.domain.Reservation;
import com.yejun.ticketreservation.domain.ReservationItem;
import com.yejun.ticketreservation.dto.ReservationDetailResponseDto;
import com.yejun.ticketreservation.dto.ReservationItemResponseDto;
import com.yejun.ticketreservation.dto.ReservationItemSummaryResponseDto;
import com.yejun.ticketreservation.dto.ReservationSummaryResponseDto;


public class ReservationMapper {

    public static ReservationDetailResponseDto toDto(Reservation reservation){

        ReservationDetailResponseDto reservationDetailDto = new ReservationDetailResponseDto();

        reservationDetailDto.setReservationId(reservation.getId());
        reservationDetailDto.setStatus(reservation.getStatus());
        reservationDetailDto.setReservedTime(reservation.getReservedTime());
        reservationDetailDto.setTotalPrice(reservation.getTotalPrice());

        reservationDetailDto.setUserId(reservation.getUser().getId());
        reservationDetailDto.setUserName(reservation.getUser().getName());
        reservationDetailDto.setUserEmail(reservation.getUser().getEmail());

        reservationDetailDto.setEventId(reservation.getEvent().getId());
        reservationDetailDto.setEventTitle(reservation.getEvent().getTitle());
        reservationDetailDto.setEventVenue(reservation.getEvent().getVenue());
        reservationDetailDto.setEventStartTime(reservation.getEvent().getStartTime());

        for (int i = 0; i < reservation.getReservationItems().size(); i++) {

            ReservationItem reservationItem = reservation.getReservationItems().get(i);

            ReservationItemResponseDto reservationItemResponseDto = new ReservationItemResponseDto();

            reservationItemResponseDto.setId(reservationItem.getId());
            reservationItemResponseDto.setTicketId(reservationItem.getTicket().getId());
            reservationItemResponseDto.setTicketType(reservationItem.getTicket().getTicketType());
            reservationItemResponseDto.setQuantity(reservationItem.getQuantity());
            reservationItemResponseDto.setUnitPrice(reservationItem.getUnitPrice());

            reservationDetailDto.getItems().add(reservationItemResponseDto);
        }

        return reservationDetailDto;
    }

    public static ReservationSummaryResponseDto toSummaryDto(Reservation reservation){

        ReservationSummaryResponseDto reservationSummaryDto = new ReservationSummaryResponseDto();

        reservationSummaryDto.setReservationId(reservation.getId());
        reservationSummaryDto.setStatus(reservation.getStatus());
        reservationSummaryDto.setTotalPrice(reservation.getTotalPrice());

        reservationSummaryDto.setEventId(reservation.getEvent().getId());
        reservationSummaryDto.setEventTitle(reservation.getEvent().getTitle());
        reservationSummaryDto.setEventVenue(reservation.getEvent().getVenue());
        reservationSummaryDto.setEventStartTime(reservation.getEvent().getStartTime());

        for (int i = 0; i < reservation.getReservationItems().size(); i++) {

            ReservationItem reservationItem = reservation.getReservationItems().get(i);

            ReservationItemSummaryResponseDto reservationItemSummaryResponseDto = new ReservationItemSummaryResponseDto();

            reservationItemSummaryResponseDto.setTicketType(reservationItem.getTicket().getTicketType());
            reservationItemSummaryResponseDto.setQuantity(reservationItem.getQuantity());

            reservationSummaryDto.getItems().add(reservationItemSummaryResponseDto);
        }

        return reservationSummaryDto;
    }
}