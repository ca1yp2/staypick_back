package com.staypick.staypick_back.service;

import com.staypick.staypick_back.dto.ReservationDto;
import com.staypick.staypick_back.entity.Reservation;
import com.staypick.staypick_back.entity.User;
import com.staypick.staypick_back.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<ReservationDto> getReservationsByUser(User user) {
        return reservationRepository.findByUserId(user.getId()).stream()
                .map(reservation -> new ReservationDto(
                        reservation.getId(),
                        reservation.getRoomName(),
                        reservation.getCheckIn().toString(),
                        reservation.getCheckOut().toString(),
                        reservation.getStatus(),
                        reservation.getAccommodation().getName()
                ))
                .toList();
    }
}
