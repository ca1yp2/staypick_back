package com.staypick.staypick_back.service;

import com.staypick.staypick_back.entity.User;
import com.staypick.staypick_back.repository.UserRepository;
import com.staypick.staypick_back.security.JwtUtil;
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
    public void register(String username, LocalDateTime birth, String userid, String password, String email, String tel, int zipcode, String address, String userimg, String userprofile, String userip) {
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

        User user = User.builder()
                .username(username)
                .birth(birth)
                .userid(userid)
                .password(encodedPassword)
                .email(email)
                .tel(tel)
                .zipcode(zipcode)
                .address(address)
                .userimg(userimg)
                .userprofile(userprofile)
                .regdate(LocalDateTime.now())
                .userip(userip)
                .role("ROLE_USER") // 기본 역할 설정
                .build();

        userRepository.save(user);
    }

    public String login(String userid, String password) {
        Optional<User> userOptional = userRepository.findByUserid(userid);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.generateToken(user.getUserid(), user.getRole());
    }

}
