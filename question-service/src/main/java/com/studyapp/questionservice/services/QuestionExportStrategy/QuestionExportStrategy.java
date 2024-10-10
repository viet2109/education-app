package com.studyapp.questionservice.services.QuestionExportStrategy;

import com.studyapp.questionservice.dto.response.QuestionResponseDto;

import java.util.List;

public interface QuestionExportStrategy {
    byte[] exportQuestions(List<QuestionResponseDto> questionResponseDtos);
}
