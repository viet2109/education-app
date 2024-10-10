package com.studyapp.cacheservice.exceptions;

import com.studyapp.cacheservice.enums.CacheError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class CacheException extends RuntimeException {
    CacheError cacheError;
    public CacheException(CacheError cacheError) {
        super(cacheError.getMessage());
        this.cacheError = cacheError;
    }
}
