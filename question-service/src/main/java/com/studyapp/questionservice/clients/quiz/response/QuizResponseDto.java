package com.studyapp.questionservice.clients.quiz.response;

import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResponseDto {
    Long id;
    String title;
    String category;
    Integer duration;
    LocalDateTime expiratedAt;
    List<QuestionResponseDto> listQuestion;
}
