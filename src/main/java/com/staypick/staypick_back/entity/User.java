package com.staypick.staypick_back.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="users")
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

    // ✅ 기본 생성자 추가
    protected User() {
    }

    // 사용자 정의 생성자
    public User(String userid, String password, String username, String tel, String email, LocalDateTime birth, String userip) {
        this.userid = userid;
        this.password = password;
        this.username = username;
        this.tel = tel;
        this.email = email;
        this.birth = birth;
        this.userip = userip;
        this.regdate = LocalDateTime.now();
        this.role = "USER";
    }

    public static User createUser(String userid, String password, String username, String tel, String email, LocalDateTime birth, String userip) {
        return new User(userid, password, username, tel, email, birth, userip);
    }
}

