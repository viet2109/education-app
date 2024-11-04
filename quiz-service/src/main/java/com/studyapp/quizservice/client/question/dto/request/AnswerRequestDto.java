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
public class AnswerRequestDto {
    String content;
    Boolean isCorrect;
    List<MultipartFile> files;
}
