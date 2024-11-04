package com.studyapp.quizservice.services.QuizImportStrategy;

import com.studyapp.quizservice.dto.response.QuizResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface QuizImportStrategy {
    QuizResponseDto convertFileToDto(MultipartFile multipartFile, String userId);
}
