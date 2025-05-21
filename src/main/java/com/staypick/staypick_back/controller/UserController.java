package com.staypick.staypick_back.controller;

import com.staypick.staypick_back.dto.KakaoLoginRequest;
import com.staypick.staypick_back.entity.User;
import com.staypick.staypick_back.repository.UserRepository;
import com.staypick.staypick_back.security.JwtUtil;
import com.staypick.staypick_back.service.KakaoAuthService;
import com.staypick.staypick_back.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final KakaoAuthService kakaoAuthService;
    private final JwtUtil jwtUtil;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> registerData, HttpServletRequest request) {
        try {
            if (!registerData.get("password").equals(registerData.get("inputRepw"))) {
                throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }

            LocalDate birth = parseBirth(registerData.get("birth"));

            userService.register(
                    request,
                    registerData.get("username"),
                    birth != null ? birth.atStartOfDay() : null,
                    registerData.get("userid"),
                    registerData.get("password"),
                    registerData.get("email"),
                    registerData.get("tel")
            );
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            logger.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("회원가입 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패");
        }
    }

    @PostMapping("/kakao-login")
    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        try {
            Map<String, Object> kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(request.getAccessToken());
            String userid = "kakao_" + kakaoUserInfo.get("id").toString();

            Optional<User> existingUser = userRepository.findByUserid(userid);
            if (existingUser.isPresent()) {
                String token = jwtUtil.generateToken(existingUser.get());
                return ResponseEntity.ok(Map.of("token", token));
            }

            return ResponseEntity.ok(Map.of(
                    "needAdditionalInfo", true,
                    "userid", userid
            ));
        } catch (Exception e) {
            logger.error("카카오 로그인 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오 로그인 실패: " + e.getMessage());
        }
    }

    @PostMapping("/kakao-register")
    public ResponseEntity<?> kakaoRegister(@RequestBody Map<String, String> registerData, HttpServletRequest request) {
        try {
            String userid = registerData.get("userid");

            if (userRepository.findByUserid(userid).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 사용자입니다.");
            }

            LocalDate birth = parseBirth(registerData.get("birth"));

            User user = userService.registerKakaoUser(
                    request,
                    registerData.get("username"),
                    birth != null ? birth.atStartOfDay() : null,
                    registerData.get("userid"),
                    registerData.get("email"),
                    registerData.get("tel")
            );

            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            logger.error("카카오 회원가입 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패");
        }
    }

    @GetMapping("/check-id/{userid}")
    public ResponseEntity<String> checkId(@PathVariable String userid) {
        try {
            boolean isAvailable = userService.isIdAvailable(userid);
            return isAvailable
                    ? ResponseEntity.ok("OK")
                    : ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 아이디입니다.");
        } catch (Exception e) {
            logger.error("아이디 중복 확인 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("아이디 중복 확인 실패");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        try {
            String token = userService.login(loginData.get("userid"), loginData.get("password"));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "로그인 실패"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);
            String userid = jwtUtil.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUserid(userid);
            return userOptional.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/check-login")
    public ResponseEntity<String> checkLoginStatus(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
            }
            return ResponseEntity.ok("로그인 상태");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 상태 확인 실패");
        }
    }

    private LocalDate parseBirth(String birthStr) {
        if (birthStr != null && !birthStr.isEmpty()) {
            return LocalDate.parse(birthStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return null;
    }
}
