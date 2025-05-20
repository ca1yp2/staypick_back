package com.staypick.staypick_back.controller;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            if (!registerData.get("password").equals(registerData.get("inputRepw"))) { // 비밀번호 재확인 검사
                throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }

            LocalDate birth = null;
            if(registerData.get("birth") != null && !registerData.get("birth").isEmpty()){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                birth = LocalDate.parse(registerData.get("birth"), formatter);
            }

            // 클라이언트 IP를 UserService의 register 메서드로 전달
            userService.register(
                    request, // HttpServletRequest 전달
                    registerData.get("username"),
                    birth != null ? birth.atStartOfDay() : null,
                    registerData.get("userid"),
                    registerData.get("password"),
                    registerData.get("email"),
                    registerData.get("tel")
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

    //아이디 중복 확인
    @GetMapping("/check-id/{userid}")
    public ResponseEntity<String> checkId(@PathVariable String userid){
        try{
            boolean isAvailable = userService.isIdAvailable(userid);
            if(isAvailable){
                return ResponseEntity.ok("OK");
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 아이디입니다.");
            }
        }catch(Exception e){
            logger.error("아이디 중복 확인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("아이디 중복 확인 실패");
        }
    }

    //로그인
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

    //카카오 로그인
    @PostMapping("/kakao-login")
    public ResponseEntity<String> kakaoLogin(@RequestBody String accessToken){
        try{
            String token = kakaoAuthService.kakaoLogin(accessToken);
            return ResponseEntity.ok(token);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오 로그인 실패");
        }
    }

    //클라이언트에서 토큰 삭제로 로그아웃 처리 (=> 백엔드 구현 x)

    //사용자 프로필
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);  // 요청 헤더에서 토큰 추출
            String userid = jwtUtil.extractUsername(token);  // JWT에서 사용자 ID 추출

            Optional<User> userOptional = userRepository.findByUserid(userid);
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    //로그인 상태
    @GetMapping("/check-login")
    public ResponseEntity<String> checkLoginStatus(HttpServletRequest request) {
        try {
            // 토큰 추출
            String token = jwtUtil.extractTokenFromRequest(request);
            
            // 토큰이 없거나 유효하지 않으면 로그인되지 않은 상태
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
            }
            
            // 토큰이 유효한 경우 로그인 상태
            return ResponseEntity.ok("로그인 상태");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 상태 확인 실패");
        }
    }
}
