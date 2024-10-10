package com.studyapp.questionservice.services.QuestionImportStrategy;

import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ExcelQuestionImportStrategy implements QuestionImportStrategy{
    @Override
    public List<QuestionRequestDto> convertFileToDto(MultipartFile multipartFile, Long examId) {
        return List.of();
    }
}
