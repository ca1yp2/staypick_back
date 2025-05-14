package com.staypick.staypick_back.controller;

import com.staypick.staypick_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> registerData) {
        try {
            if (!registerData.get("password").equals(registerData.get("inputRepw"))) { // 비밀번호 재확인 검사
                throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }
            userService.register(
                    registerData.get("username"),
                    registerData.get("birth") != null ? LocalDateTime.parse(registerData.get("birth")) : null,
                    registerData.get("userid"),
                    registerData.get("password"),
                    registerData.get("email"),
                    registerData.get("tel"),
                    registerData.get("zipcode") != null ? Integer.parseInt(registerData.get("zipcode")) : null,
                    registerData.get("address"),
                    registerData.get("userimg"),
                    registerData.get("userprofile"),
                    registerData.get("userip")
            );
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            logger.error("회원가입 실패 (IllegalArgumentException): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("회원가입 실패 (Exception): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
        try {
            String token = userService.login(loginData.get("userid"), loginData.get("password"));
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 실패");
        }
    }
}
