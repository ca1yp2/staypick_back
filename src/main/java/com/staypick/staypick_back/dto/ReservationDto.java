package com.staypick.staypick_back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationDto {
    private Long id;
    private String roomName;
    private String checkIn;
    private String checkOut;
    private String status;
    private String accommodationName;
}
