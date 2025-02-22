package com.studyapp.examhistoryservice.client.quiz.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question {
    Long id;
    String content;
    List<Media> files;
    List<Answer> listAnswer;
    LocalDateTime updatedAt;
}
