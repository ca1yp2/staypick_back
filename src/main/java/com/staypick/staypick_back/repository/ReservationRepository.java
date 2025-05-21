package com.staypick.staypick_back.repository;

import com.staypick.staypick_back.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userid); // 현재 로그인 사용자 기준 예약 조회
}
