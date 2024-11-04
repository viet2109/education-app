package com.studyapp.quizservice.client.question.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequestFeignDto {
    String content;

    Boolean isCorrect;
    List<Long> filesIndex;
}
