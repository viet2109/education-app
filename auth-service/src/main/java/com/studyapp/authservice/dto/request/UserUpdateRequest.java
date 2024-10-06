package com.studyapp.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @NotBlank(message = "The email is mandatory")
    @Email(message = "The email has invalid")
    String email;

    @NotBlank(message = "The username is mandatory")
    String name;
}
