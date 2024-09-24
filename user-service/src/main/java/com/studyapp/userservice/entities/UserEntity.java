package com.studyapp.userservice.entities;

import com.studyapp.userservice.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class UserEntity {

    @Id
    String id;

    @NotNull(message = "name must not be null")
    String name;

    @Indexed(unique = true)
    String email;

    @Indexed(unique = true)
    String phone;
    String password;

    @Field(targetType = FieldType.STRING)
    List<Role> roles;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;

    boolean isActive;
}
