package com.studyapp.quizservice.services.QuizImportStrategy;

import com.studyapp.quizservice.dto.request.QuizImportRequestDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelQuizImportStrategy implements QuizImportStrategy {
    @Override
    public QuizResponseDto convertFileToDto(MultipartFile multipartFile, String userId) {
        return null;
    }
}
