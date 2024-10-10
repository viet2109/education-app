package com.studyapp.authservice.client;

import com.studyapp.authservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "cache-service", path = "/cache", configuration = FeignConfig.class)
public interface CacheClient {
    @PostMapping("/token/refresh")
    ResponseEntity<Void> saveRefreshToken(@RequestParam String userId, @RequestParam String sessionId, @RequestParam String refreshToken);

    @GetMapping("/token/refresh")
    ResponseEntity<String> getRefreshToken(@RequestParam String userId);

    @DeleteMapping("/token/refresh")
    ResponseEntity<Void> deleteRefreshToken(@RequestParam String userId);

    @GetMapping("/token/exists")
    ResponseEntity<Boolean> refreshTokenExists(@RequestParam String userId);
}
