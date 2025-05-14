package com.staypick.staypick_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staypick.staypick_back.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserid(String userid);
    boolean existsByUserid(String userid);
    boolean existsByEmail(String email);
    boolean existsByTel(String tel);
}
