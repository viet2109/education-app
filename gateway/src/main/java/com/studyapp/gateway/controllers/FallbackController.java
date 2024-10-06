package com.studyapp.gateway.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/user")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("User service is unavailable. Please try again later.");
    }

    @RequestMapping("/question")
    public ResponseEntity<String> questionFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Question service is unavailable. Please try again later.");
    }

    @RequestMapping("/quiz")
    public ResponseEntity<String> quizFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Quiz service is unavailable. Please try again later.");
    }

    @RequestMapping("/auth")
    public ResponseEntity<String> authFallback(Throwable throwable) {
        log.error("Fallback triggered for Auth Service. Cause: {}", throwable.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Auth service is unavailable. Please try again later.");
    }

    @RequestMapping("/file")
    public ResponseEntity<String> fileFallback(Throwable throwable) {
        log.error("Fallback triggered for File Service. Cause: {}", throwable.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("File service is unavailable. Please try again later.");
    }
}
