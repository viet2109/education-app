package com.studyapp.cacheservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.studyapp.cacheservice.enums.CacheError;
import com.studyapp.cacheservice.exceptions.CacheException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RefreshTokenService {
    String REFRESH_TOKEN_PREFIX = "refresh_token";
    RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String userId, String sessionId, String refreshToken) {
        String key = String.format("%s:%s:%s", REFRESH_TOKEN_PREFIX, userId, sessionId);
        long ttlInSeconds = getExpirationFromToken(refreshToken);
        redisTemplate.opsForValue().set(key, refreshToken, ttlInSeconds, TimeUnit.SECONDS);
    }

    // Lấy refresh token từ Redis
    public String getRefreshToken(String userId, String sessionId) {
        if (refreshTokenExists(userId, sessionId)) throw new CacheException(CacheError.KEY_NOT_FOUND);
        String key = String.format("%s:%s:%s", REFRESH_TOKEN_PREFIX, userId, sessionId);
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa refresh token khỏi Redis
    public void deleteRefreshToken(String userId, String sessionId) {
        if (refreshTokenExists(userId, sessionId)) throw new CacheException(CacheError.KEY_NOT_FOUND);
        String key = String.format("%s:%s:%s", REFRESH_TOKEN_PREFIX, userId, sessionId);
        redisTemplate.delete(key);
    }

    // Kiểm tra refresh token có tồn tại không
    public boolean refreshTokenExists(String userId, String sessionId) {
        String key = String.format("%s:%s:%s", REFRESH_TOKEN_PREFIX, userId, sessionId);
        return !Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public long getExpirationFromToken(String token) {
        // Giải mã JWT
        DecodedJWT decodedJWT = JWT.decode(token);

        // Lấy thông tin về thời gian hết hạn (expiration time)
        Date expirationDate = decodedJWT.getExpiresAt();

        // Tính số giây còn lại từ thời điểm hiện tại đến khi hết hạn
        long currentTimeInMillis = System.currentTimeMillis();
        long expirationTimeInMillis = expirationDate.getTime();

        // Trả về TTL bằng giây
        return (expirationTimeInMillis - currentTimeInMillis) / 1000;
    }
}
