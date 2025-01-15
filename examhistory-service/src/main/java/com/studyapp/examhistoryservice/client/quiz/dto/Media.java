package com.studyapp.examhistoryservice.client.quiz.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Media {
    Long id;
    String filename;
    String fileUrl;
    String fileType;
    long sizeInBytes;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
}
