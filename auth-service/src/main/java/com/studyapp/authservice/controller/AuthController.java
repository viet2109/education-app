package com.studyapp.authservice.controller;

import com.studyapp.authservice.dto.request.UserLoginRequest;
import com.studyapp.authservice.dto.request.UserRequest;
import com.studyapp.authservice.dto.request.UserUpdateRequest;
import com.studyapp.authservice.dto.response.UserResponse;
import com.studyapp.authservice.services.UserKeyCloakService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    UserKeyCloakService userKeyCloakService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userKeyCloakService.register(userRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponse.getId())
                .toUri();
        return ResponseEntity.created(location).body(String.format("User with id %s created successfully", userResponse.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid UserLoginRequest loginRequest) {
        Map<String, Object> token = userKeyCloakService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/password/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Email(message = "The email is invalid") @Valid String email) {
        userKeyCloakService.forgotPassword(email);
        return ResponseEntity.ok("We have sent an email to you");
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest updateRequest) {
        userKeyCloakService.updateUserProfileAuth(id, updateRequest);
        return ResponseEntity.ok(String.format("User with id %s updated successfully", id));
    }

    @PatchMapping("/users/deactivate/{id}")
    public ResponseEntity<String> deActiveUser(@PathVariable String id) {
        userKeyCloakService.deActiveUser(id);
        return ResponseEntity.ok(String.format("User with id %s deactivate successfully", id));
    }

    @PatchMapping("/users/activate/{id}")
    public ResponseEntity<String> activeUser(@PathVariable String id) {
        userKeyCloakService.activeUser(id);
        return ResponseEntity.ok(String.format("User with id %s activate successfully", id));
    }

    @PostMapping("/users/logout/{id}")
    public ResponseEntity<Void> logout(@PathVariable String id) {
        userKeyCloakService.logout(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token/{id}")
    public ResponseEntity<String> refreshToken(@PathVariable String id) {
        String token = userKeyCloakService.refreshAccessToken(id);
        return ResponseEntity.ok(token);
    }
}

