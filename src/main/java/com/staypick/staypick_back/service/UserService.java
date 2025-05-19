package com.staypick.staypick_back.service;

import com.staypick.staypick_back.entity.User;
import com.staypick.staypick_back.repository.UserRepository;
import com.staypick.staypick_back.security.JwtUtil;
import com.staypick.staypick_back.util.IpUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void register(HttpServletRequest request, String username, LocalDateTime birth, String userid, String password, String email, String tel) {
        
        //클라이언트 IP 추출
        String userip = IpUtils.getClientIp(request);

        if (userRepository.existsByUserid(userid)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByTel(tel)) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = User.createUser(userid, encodedPassword, username, tel, email, birth, userip);

        userRepository.save(user);
    }

    public boolean isIdAvailable(String userid){
        return !userRepository.existsByUserid(userid);
    }

    public String login(String userid, String password) {
        try {
            Optional<User> userOptional = userRepository.findByUserid(userid);
            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
            }

            User user = userOptional.get();
            System.out.println("User found: " + user.getUserid()); // 디버깅용 로그

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            System.out.println("Generated Token: " + token); // 디버깅용 로그

            return token;
        } catch (Exception e) {
            // 예외 발생 시 로그 남기기
            System.err.println("로그인 오류: " + e.getMessage());
            throw e;
        }
    }


}
