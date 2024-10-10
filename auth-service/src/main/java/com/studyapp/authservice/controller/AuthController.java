package com.studyapp.authservice.controller;

import com.studyapp.authservice.dto.request.UserLoginRequest;
import com.studyapp.authservice.dto.request.UserRequest;
import com.studyapp.authservice.dto.request.UserUpdateRequest;
import com.studyapp.authservice.dto.response.UserResponse;
import com.studyapp.authservice.enums.AuthError;
import com.studyapp.authservice.exception.AuthException;
import com.studyapp.authservice.services.UserKeyCloakService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid UserLoginRequest loginRequest, HttpServletResponse response) {
        Map<String, Object> token = userKeyCloakService.login(loginRequest);
        Cookie cookie = new Cookie("sessionId", (String) token.get("sessionId"));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(30 * 24 * 60 * 60);//30 ngày
        response.addCookie(cookie);
        token.remove("sessionId");
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

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<String> deActiveUser(@PathVariable String id) {
        userKeyCloakService.deActiveUser(id);
        return ResponseEntity.ok(String.format("User with id %s deactivate successfully", id));
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<String> activeUser(@PathVariable String id) {
        userKeyCloakService.activeUser(id);
        return ResponseEntity.ok(String.format("User with id %s activate successfully", id));
    }

    @PostMapping("/users/{id}/logout")
    public ResponseEntity<Void> logout(@PathVariable String id) {
        userKeyCloakService.logout(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/refresh-token")
    public ResponseEntity<String> refreshToken(@PathVariable String id, HttpServletRequest request) {
        AtomicReference<String> sessionId = new AtomicReference<>("");
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            throw new AuthException(AuthError.TOKEN_INVALID);
        }

        Arrays.stream(cookies)
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .findFirst()
                .ifPresentOrElse(cookie -> {
                    sessionId.set(cookie.getValue());
                }, () -> {
                    throw new AuthException(AuthError.TOKEN_INVALID);
                });
        String token = userKeyCloakService.refreshAccessToken(id, sessionId.get());
        return ResponseEntity.ok(token);
    }
}

