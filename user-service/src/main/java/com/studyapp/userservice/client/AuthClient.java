package com.studyapp.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "auth-service", path = "/auth")
public interface AuthClient {
    @PutMapping("/users/{id}")
    ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody UserUpdateAuthProfileRequest updateRequest);
}
