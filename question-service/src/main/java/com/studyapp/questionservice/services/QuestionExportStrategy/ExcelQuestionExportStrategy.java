package com.studyapp.questionservice.services.QuestionExportStrategy;

import com.studyapp.questionservice.dto.response.QuestionResponseDto;

import java.util.List;

public class ExcelQuestionExportStrategy implements QuestionExportStrategy{
    @Override
    public byte[] exportQuestions(List<QuestionResponseDto> questionResponseDtos) {
        return new byte[0];
    }
}
