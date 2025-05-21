package com.staypick.staypick_back.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.staypick.staypick_back.entity.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    // SigningKey 생성
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "HmacSHA512");
    }

    // 토큰 생성
    public String generateToken(User user) {
        boolean needAdditionalInfo = user.getEmail() == null || user.getBirth() == null || user.getTel() == null;
        return Jwts.builder()
                .subject(user.getUserid())  // username을 subject로 사용
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("needAdditionalInfo", needAdditionalInfo)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(String userid, String username, String role) {
        User tempUser = new User();
        tempUser.setUserid(userid);
        tempUser.setUsername(username);
        tempUser.setRole(role);
        // email, birth, tel 은 null 그대로, 추가 입력 필요 여부 판단 가능
        return generateToken(tempUser);
    }
    
    //토큰에서 사용자 이름 추출
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //토큰에서 역할 추출
    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    //토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    //HttpServletRequest에서 토큰 추출
    public String extractTokenFromRequest(HttpServletRequest request){
        //Authorization 헤더에서 "Bearer" 접두어를 제외한 토큰을 추출
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
            return authorizationHeader.substring(7); //"Bearer" 접두어 제거
        }
        return null;
    }

    //RefreshToekn
    public String generateRefreshToken(String userid){
        return Jwts.builder()
                   .subject(userid)
                   .expiration(new Date(System.currentTimeMillis() + ( 7 * 24 * 60 * 60 * 1000))) //7일
                   .signWith(getSigningKey())
                   .compact();
    }

}
