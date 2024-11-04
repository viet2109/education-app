package com.studyapp.questionservice.clients.file.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Media {
    Long id;
    String filename;
    String fileUrl;
    String fileType;
    long sizeInBytes;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
}
