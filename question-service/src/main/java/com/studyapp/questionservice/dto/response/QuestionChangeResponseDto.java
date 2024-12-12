package com.studyapp.questionservice.dto.response;

import com.studyapp.questionservice.clients.file.dto.Media;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChangeResponseDto {
    Long id;
    String content;
    List<Media> files;
    List<AnswerChangeResponseDto> listAnswer;
    LocalDateTime updatedAt;
}
