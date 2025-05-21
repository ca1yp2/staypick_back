package com.staypick.staypick_back.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staypick.staypick_back.dto.KakaoUserResponse;
import com.staypick.staypick_back.entity.User;
import com.staypick.staypick_back.repository.UserRepository;
import com.staypick.staypick_back.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public KakaoAuthService(UserRepository userRepository, JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
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
        ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(
            kakaoApiUrl, 
            HttpMethod.GET,
            entity,
            KakaoUserResponse.class
        );
        

        // 응답에서 카카오 사용자 정보 추출
        KakaoUserResponse userResponse = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("카카오 응답 JSON (디버깅용): " + objectMapper.writeValueAsString(response.getBody()));

        if(userResponse == null || userResponse.getKakaoAccount() == null || userResponse.getKakaoAccount().getProfile() == null){
            throw new Exception("카카오 사용자 정보를 가져올 수 없습니다.");
        }
        
        // 카카오 아이디와 이름을 추출
        String kakaoId = String.valueOf(userResponse.getId());
        String username = Optional.ofNullable(userResponse.getKakaoAccount().getProfile()).map(KakaoUserResponse.Profile::getNickname).orElse(Optional.ofNullable(userResponse.getKakaoAccount().getEmail()).orElse("카카오사용자"));

        // 사용자 정보가 이미 존재하는지 확인
        Optional<User> existingUser = userRepository.findByUserid(kakaoId);
        User user;

        if (existingUser.isPresent()) {
            // 기존 사용자라면 정보 업데이트 또는 로그인
            user = existingUser.get();
        } else {
            // 새로운 사용자라면 등록
            user = User.builder()
                    .userid(kakaoId)  // 카카오 ID를 userid로 사용
                    .username(username)  // 닉네임
                    .password(null)       // 카카오 사용자는 비밀번호 없음
                    .email(null)
                    .tel(null)
                    .birth(null)
                    .userip(null)
                    .build();

            user.setRole("USER");
            userRepository.save(user);
        }

        // JWT 토큰 발급
        return jwtUtil.generateToken(user.getUserid() ,user.getUsername(), user.getRole());
    }

    public Map<String, Object> getKakaoUserInfo(String accessToken) throws Exception {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> result = response.getBody();
        if (result == null || result.get("id") == null) {
            throw new Exception("카카오 사용자 정보를 불러오지 못했습니다.");
        }

        Object kakaoAccountObj = result.get("kakao_account");
        Map<String, Object> kakaoAccount = kakaoAccountObj instanceof Map ? (Map<String, Object>) kakaoAccountObj : new HashMap<>();

        Object profileObj = kakaoAccount.get("profile");
        Map<String, Object> profile = profileObj instanceof Map ? (Map<String, Object>) profileObj : null;

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", result.get("id"));
        userInfo.put("nickname", profile != null ? profile.get("nickname") : "카카오사용자");

        return userInfo;
    }
}
