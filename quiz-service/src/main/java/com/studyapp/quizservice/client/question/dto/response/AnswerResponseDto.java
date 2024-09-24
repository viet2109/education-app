package com.studyapp.quizservice.client.question.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDto {
    Long id;
    String content;
}
