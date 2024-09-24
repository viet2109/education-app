package com.studyapp.questionservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDto {
    Long id;
    String content;
    List<AnswerResponseDto> listAnswer;
    Long examId;
}
