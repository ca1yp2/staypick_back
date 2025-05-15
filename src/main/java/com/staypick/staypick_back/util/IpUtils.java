package com.staypick.staypick_back.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {
     // 클라이언트 IP를 추출하는 메서드
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
