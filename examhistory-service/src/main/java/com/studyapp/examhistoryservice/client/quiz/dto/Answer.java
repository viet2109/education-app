package com.studyapp.examhistoryservice.client.quiz.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Answer {
    Long id;
    String content;
    Boolean isCorrect;
    List<Media> files;
}
