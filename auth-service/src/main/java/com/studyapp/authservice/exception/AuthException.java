package com.studyapp.authservice.exception;

import com.studyapp.authservice.enums.AuthError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class AuthException extends RuntimeException {
    AuthError authError;
    public AuthException(AuthError authError) {
        super(authError.getMessage());
        this.authError = authError;
    }
}
