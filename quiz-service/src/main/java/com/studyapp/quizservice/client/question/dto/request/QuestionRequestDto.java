package com.studyapp.quizservice.client.question.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDto {
    String content;
    List<AnswerRequestDto> listAnswer;
    Long examId;
    List<MultipartFile> files;
}
