package com.studyapp.questionservice.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionDto {
    Long id;
    String content;
    Long createdBy;
}
