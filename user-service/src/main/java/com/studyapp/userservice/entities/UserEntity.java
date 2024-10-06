package com.studyapp.userservice.entities;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class UserEntity {

    @Id
    String id;
    String name;

    @Indexed(unique = true)
    String email;

    @Indexed(unique = true)
    String phone;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;
}
