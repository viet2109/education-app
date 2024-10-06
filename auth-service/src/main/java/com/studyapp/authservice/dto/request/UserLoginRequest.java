package com.studyapp.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginRequest {
    @NotBlank(message = "The email is mandatory")
    String email;

    @NotBlank(message = "The password is mandatory")
    String password;
}
