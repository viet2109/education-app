package com.studyapp.quizservice.client.user;

import com.studyapp.quizservice.client.user.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user-service", path = "/users")
public interface UserClient {
    @GetMapping("/{id}")
    ResponseEntity<UserResponse> findUserById(@PathVariable String id);
}
