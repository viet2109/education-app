package com.studyapp.quizservice.dto.response;

import com.studyapp.quizservice.client.question.dto.response.QuestionResponseDto;
import com.studyapp.quizservice.enums.Category;
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
    Category category;
    Integer duration;
    LocalDateTime expiratedAt;
    List<QuestionResponseDto> listQuestion;
}
