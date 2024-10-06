package com.studyapp.userservice.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiClientExceptionHandler {

    @ExceptionHandler(RestApiClientException.class)
    public ResponseEntity<?> handleRestApiClientException(RestApiClientException exception) {
        return new ResponseEntity<>(exception.parseResponseBody(), exception.getStatus());
    }

}
