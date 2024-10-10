package com.studyapp.cacheservice.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum CacheError {
    KEY_NOT_FOUND("Key not found", HttpStatus.NOT_FOUND),
    COOKIE_NOT_FOUND("Cookie not found", HttpStatus.NOT_FOUND),
    ;

    String message;
    HttpStatus httpStatus;
}
