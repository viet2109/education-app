package com.studyapp.quizservice.services.QuestionExportStrategy;

import com.studyapp.quizservice.dto.response.QuizResponseDto;

public class ExcelQuizExportStrategy implements QuizExportStrategy {
    @Override
    public byte[] exportQuiz(QuizResponseDto quizResponseDto) {
        return new byte[0];
    }
}
