package com.studyapp.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserRequest {

    @NotBlank(message = "The name is mandatory")
    String name;

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The email has invalid")
    String email;

    @NotBlank(message = "The phone is mandatory")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$", message = "The phone number has invalid")
    String phone;
}
