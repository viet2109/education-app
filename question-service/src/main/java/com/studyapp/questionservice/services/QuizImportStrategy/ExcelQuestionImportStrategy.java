package com.studyapp.questionservice.services.QuizImportStrategy;

import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ExcelQuestionImportStrategy implements QuestionImportStrategy {

    @Override
    public List<QuestionResponseDto> convertFileToDto(MultipartFile multipartFile, Long examId) {
        return List.of();
    }
}
