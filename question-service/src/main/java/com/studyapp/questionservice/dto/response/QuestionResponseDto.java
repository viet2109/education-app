package com.studyapp.questionservice.dto.response;

import com.studyapp.questionservice.clients.file.dto.Media;
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
    List<Media> files;
}
