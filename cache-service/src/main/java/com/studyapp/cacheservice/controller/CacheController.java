package com.studyapp.cacheservice.controller;

import com.studyapp.cacheservice.enums.CacheError;
import com.studyapp.cacheservice.exceptions.CacheException;
import com.studyapp.cacheservice.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CacheController {
    RefreshTokenService refreshTokenService;

    // Lưu refresh token vào Redis với TTL
    @PostMapping("/token/refresh")
    public ResponseEntity<Void> saveRefreshToken(@RequestParam String userId, @RequestParam String sessionId, @RequestParam String refreshToken) {
        refreshTokenService.saveRefreshToken(userId, sessionId, refreshToken);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{sessionId}")
                .buildAndExpand(sessionId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // Lấy refresh token từ Redis
    @GetMapping("/token/refresh")
    public ResponseEntity<String> getRefreshToken(@RequestParam String userId, HttpServletRequest request) {
        AtomicReference<String> sessionId = new AtomicReference<>("");
        Cookie[] cookies = request.getCookies();
        log.info("cookie: " + Arrays.toString(cookies));
        if (cookies == null || cookies.length == 0) {
            throw new CacheException(CacheError.COOKIE_NOT_FOUND);
        }

        Arrays.stream(cookies)
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .findFirst()
                .ifPresentOrElse(cookie -> {
                    sessionId.set(cookie.getValue());
                }, () -> {
                    throw new CacheException(CacheError.COOKIE_NOT_FOUND);
                });
        return ResponseEntity.ok(refreshTokenService.getRefreshToken(userId, sessionId.get()));
    }

    // Xóa refresh token khỏi Redis
    @DeleteMapping("/token/refresh")
    public ResponseEntity<Void> deleteRefreshToken(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {
        AtomicReference<String> sessionId = new AtomicReference<>("");
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            throw new CacheException(CacheError.COOKIE_NOT_FOUND);
        }

        Arrays.stream(cookies)
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .findFirst()
                .ifPresentOrElse(cookie -> {
                    sessionId.set(cookie.getValue());
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }, () -> {
                    throw new CacheException(CacheError.COOKIE_NOT_FOUND);
                });
        refreshTokenService.deleteRefreshToken(userId, sessionId.get());
        return ResponseEntity.ok().build();
    }

    // Kiểm tra refresh token có tồn tại không
    @GetMapping("/token/exists")
    public ResponseEntity<Boolean> refreshTokenExists(@RequestParam String userId, HttpServletRequest request) {
        AtomicReference<String> sessionId = new AtomicReference<>("");
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            throw new CacheException(CacheError.COOKIE_NOT_FOUND);
        }

        Arrays.stream(cookies)
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .findFirst()
                .ifPresentOrElse(cookie -> {
                    sessionId.set(cookie.getValue());
                }, () -> {
                    throw new CacheException(CacheError.COOKIE_NOT_FOUND);
                });
        return ResponseEntity.ok(refreshTokenService.refreshTokenExists(userId, sessionId.get()));
    }
}
