package com.staypick.staypick_back.service;

import com.staypick.staypick_back.entity.RefreshToken;
import com.staypick.staypick_back.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String userid, String token) {
        refreshTokenRepository.save(new RefreshToken(userid, token));
    }

    public void deleteRefreshToken(String userid) {
        refreshTokenRepository.deleteById(userid);
    }

    public String getRefreshToken(String userid) {
        return refreshTokenRepository.findById(userid)
                .map(RefreshToken::getToken)
                .orElse(null);
    }
}
