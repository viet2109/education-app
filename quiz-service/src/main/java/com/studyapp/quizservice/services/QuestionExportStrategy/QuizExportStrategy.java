package com.studyapp.quizservice.services.QuestionExportStrategy;

import com.studyapp.quizservice.dto.response.QuizResponseDto;

public interface QuizExportStrategy {
    byte[] exportQuiz(QuizResponseDto quizResponseDto);
}
