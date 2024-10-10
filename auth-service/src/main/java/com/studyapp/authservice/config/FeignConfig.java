package com.studyapp.authservice.config;

import com.studyapp.authservice.exception.CustomErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    StringBuilder cookieHeader = new StringBuilder();
                    for (Cookie cookie : cookies) {
                        cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
                    }
                    if (!cookieHeader.isEmpty()) {
                        cookieHeader.setLength(cookieHeader.length() - 2); // Xóa dấu "; " cuối cùng
                    }
                    requestTemplate.header("Cookie", cookieHeader.toString());
                }
            }
        };
    }
}
