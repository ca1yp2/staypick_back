package com.staypick.staypick_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staypick.staypick_back.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

}
