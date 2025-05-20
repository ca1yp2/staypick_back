package com.staypick.staypick_back.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staypick.staypick_back.entity.User;
import com.staypick.staypick_back.repository.UserRepository;
import com.staypick.staypick_back.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public KakaoAuthService(UserRepository userRepository, JwtUtil jwtUtil, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String kakaoLogin(String accessToken) throws Exception {
        // 카카오 API 호출하여 사용자 정보 가져오기
        String kakaoApiUrl = "https://kapi.kakao.com/v2/user/me";
        
        // 헤더에 access token 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // HttpEntity 객체 생성 (본문 없이 헤더만)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // exchange()를 사용해 GET 요청 전송
        ResponseEntity<String> response = restTemplate.exchange(
            kakaoApiUrl, 
            HttpMethod.GET,
            entity,
            String.class
        );

        // 응답 피싱
        JsonNode userNode = objectMapper.readTree(response.getBody());
        
        // 카카오 아이디와 이름을 추출
        String kakaoId = userNode.get("id").asText();
        String username = userNode.path("properties").path("nickname").asText();

        // 사용자 정보가 이미 존재하는지 확인
        Optional<User> existingUser = userRepository.findByUserid(kakaoId);
        User user;

        if (existingUser.isPresent()) {
            // 기존 사용자라면 정보 업데이트 또는 로그인
            user = existingUser.get();
        } else {
            // 새로운 사용자라면 등록
            user = User.createUser(kakaoId, null, username, null, null, null, null);

            userRepository.save(user);
        }

        // JWT 토큰 발급
        return jwtUtil.generateToken(user.getUserid() ,user.getUsername(), user.getRole());
    }
}
