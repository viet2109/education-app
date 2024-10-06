package com.studyapp.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class FileExceptionHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleAppException(IOException exception) {
        return new ResponseEntity<>(buildResponse(exception, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> buildResponse(Exception exception, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("errorMessage", exception.getMessage());
        response.put("status", status.name());
        return response;
    }
}
