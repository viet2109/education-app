package com.studyapp.questionservice.services.QuizImportStrategy;

import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionImportStrategy {
    List<QuestionResponseDto> convertFileToDto(MultipartFile multipartFile, Long examId);
}
