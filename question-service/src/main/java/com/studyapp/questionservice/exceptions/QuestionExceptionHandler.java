package com.studyapp.questionservice.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class QuestionExceptionHandler {

    @ExceptionHandler(QuestionException.class)
    public ResponseEntity<Map<String, String>> handleAppException(QuestionException exception) {
        HttpStatus status = exception.getQuestionError().getHttpStatus();
        return new ResponseEntity<>(buildResponse(exception, status), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, error -> {
            String defaultMessage = error.getDefaultMessage();
            return defaultMessage != null ? defaultMessage : "Validation error";
        }));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> {
                            String fullPath = violation.getPropertyPath().toString();
                            return fullPath.substring(fullPath.lastIndexOf('.') + 1);
                        },
                        ConstraintViolation::getMessage
                ));
        return ResponseEntity.badRequest().body(errors);
    }


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private Map<String, String> buildResponse(Exception exception, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("errorMessage", exception.getMessage());
        response.put("status", status.name());
        return response;
    }
}
