package com.studyapp.examhistoryservice.client.quiz.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Quiz {
    Long id;
    String title;
    Category category;
    Integer duration;
    LocalDateTime expiratedAt;
    LocalDateTime updatedAt;
    List<Question> listQuestion;
}
