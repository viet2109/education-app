package com.studyapp.userservice.client;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateAuthProfileRequest {
    String email;
    String name;
}
