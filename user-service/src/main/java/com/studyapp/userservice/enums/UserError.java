package com.studyapp.userservice.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum UserError {
    ALREADY_EXIST_EMAIL("Email already exists", HttpStatus.CONFLICT),
    ALREADY_EXIST_PHONE("Phone number already exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("Invalid password", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED("Email not verified", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED("Account is locked", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_ACCESS("Unauthorized access", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    String message;
    HttpStatus httpStatus;
}
