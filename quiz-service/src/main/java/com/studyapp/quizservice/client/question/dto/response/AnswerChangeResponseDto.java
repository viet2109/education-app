package com.studyapp.quizservice.client.question.dto.response;

import com.studyapp.quizservice.client.file.dto.Media;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerChangeResponseDto {
    Long id;
    String content;
    Boolean isCorrect;
    List<Media> files;
}
