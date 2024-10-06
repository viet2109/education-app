package com.studyapp.authservice.client;

import com.studyapp.authservice.config.FeignConfig;
import com.studyapp.authservice.dto.request.UserRequest;
import com.studyapp.authservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "user-service", path = "/users", configuration = FeignConfig.class)
public interface UserClient {
    @PostMapping
    ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest);

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> findUserById(@PathVariable String id);

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteUser(@PathVariable String id);
}
