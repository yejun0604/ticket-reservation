package com.yejun.ticketreservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReservationCreateRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long eventId;

    @NotEmpty
    private List<@Valid ReservationItemRequestDto> items;
}
