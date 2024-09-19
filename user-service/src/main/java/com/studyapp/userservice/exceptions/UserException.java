package com.studyapp.userservice.exceptions;

import com.studyapp.userservice.enums.UserError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class UserException extends RuntimeException {
    UserError userError;
    public UserException(UserError userError) {
        super(userError.getMessage());
        this.userError = userError;
    }
}
