package com.studyapp.notificationservice.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEntity {

    @Id
    String id;
    LocalDateTime createdAt;

}
