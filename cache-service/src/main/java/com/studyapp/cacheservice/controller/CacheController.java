package com.studyapp.cacheservice.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CacheController {

    RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    // Lưu refresh token vào Redis với TTL
    @PostMapping("/token/refresh")
    public ResponseEntity<Void> saveRefreshToken(@RequestParam String userId, @RequestParam String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        long ttlInSeconds = 30L * 24L * 60L * 60L; // 30 ngày
        redisTemplate.opsForValue().set(key, refreshToken, ttlInSeconds, TimeUnit.SECONDS);
        return ResponseEntity.ok().build();
    }

    // Lấy refresh token từ Redis
    @GetMapping("/token/refresh")
    public ResponseEntity<String> getRefreshToken(@RequestParam String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        String token = redisTemplate.opsForValue().get(key);
        return token != null ? ResponseEntity.ok(token) : ResponseEntity.notFound().build();
    }

    // Xóa refresh token khỏi Redis
    @DeleteMapping("/token/refresh")
    public ResponseEntity<Void> deleteRefreshToken(@RequestParam String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
        return ResponseEntity.ok().build();
    }

    // Kiểm tra refresh token có tồn tại không
    @GetMapping("/token/exists")
    public ResponseEntity<Boolean> refreshTokenExists(@RequestParam String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        return ResponseEntity.ok(exists);
    }
}
