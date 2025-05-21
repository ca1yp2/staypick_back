package com.staypick.staypick_back.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userid;
    private String password;
    private String username;
    private String tel;
    private String email;
    private LocalDateTime birth;
    private LocalDateTime regdate;
    private String userip;
    private String role;
    private String provider;

    public User() {
    }

    @Builder
    public User(String userid, String password, String username, String tel, String email, LocalDateTime birth, String userip, String provider) {
        this.userid = userid;
        this.password = password;
        this.username = username;
        this.tel = tel;
        this.email = email;
        this.birth = birth;
        this.userip = userip;
        this.provider = provider;
        this.regdate = LocalDateTime.now();
    }

    public static User createUser(String userid, String password, String username, String tel, String email, LocalDateTime birth, String userip, String provider) {
        return new User(userid, password, username, tel, email, birth, userip, provider);
    }

    public void updateAdditionalInfo(String password, String tel, String email, LocalDateTime birth, String userip) {
        this.password = password;
        this.tel = tel;
        this.email = email;
        this.birth = birth;
        this.userip = userip;
    }
}
