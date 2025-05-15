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

    // 기본 생성자
    public User() {
        this.regdate = LocalDateTime.now();  // regdate는 회원가입 시점으로 자동 설정
        this.role = "USER";  // role은 기본값 "USER"
    }

    // 팩토리 메서드
    public static User createUser(String userid, String password, String username, String tel, String email, LocalDateTime birth, String userip) {
        User user = new User();
        user.setUserid(userid);
        user.setPassword(password);
        user.setUsername(username);
        user.setTel(tel);
        user.setEmail(email);
        user.setBirth(birth);
        user.setUserip(userip);
        user.setRegdate(LocalDateTime.now());  // regdate는 현재 시간으로 설정
        user.setRole("USER");  // role은 기본값으로 "USER"로 설정
        return user;
    }
}
