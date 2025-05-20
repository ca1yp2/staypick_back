package com.staypick.staypick_back.controller;

import com.staypick.staypick_back.dto.ReservationDto;
import com.staypick.staypick_back.entity.User;
import com.staypick.staypick_back.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mypage/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationDto> getMyReservations(@AuthenticationPrincipal User user) {
        return reservationService.getReservationsByUser(user);
    }
}
