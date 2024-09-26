package com.studyapp.gateway.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("User service is unavailable. Please try again later.");
    }

    @GetMapping("/question")
    public ResponseEntity<String> questionFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Question service is unavailable. Please try again later.");
    }

    @GetMapping("/quiz")
    public ResponseEntity<String> quizFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Quiz service is unavailable. Please try again later.");
    }
}
