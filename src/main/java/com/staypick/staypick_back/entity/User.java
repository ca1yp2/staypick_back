package com.staypick.staypick_back.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="users")
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private LocalDateTime birth;
    private String userid;
    private String password;
    private String email;
    private String tel;
    private Integer zipcode;
    private String address;
    private String userimg;
    private String userprofile;
    private LocalDateTime regdate;
    private String userip;
    private String role;

}
