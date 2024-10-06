package com.studyapp.authservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum AuthError {
    INVALID_CREDENTIALS("Invalid credentials, please check your username and password.", HttpStatus.BAD_REQUEST),
    DISABLED_ACCOUNT("Your account has been disabled, please contact support.", HttpStatus.FORBIDDEN),
    UNVERIFIED_EMAIL("Email address is not verified. Please verify your email.", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("User not found, please register or check your details.", HttpStatus.NOT_FOUND),
    ACCESS_DENIED("Access denied. You do not have the necessary permissions.", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("Token expired, please login again.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("Invalid token, please try again.", HttpStatus.UNAUTHORIZED),
    USER_ALREADY_EXISTS("User already exists, please login or reset your password.", HttpStatus.CONFLICT),
    KEYCLOAK_ERROR("Error while communicating with Keycloak.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR("An unknown error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

    String message;
    HttpStatus httpStatus;
}

