package com.studyapp.quizservice.services.QuizExportStrategy;

import com.studyapp.quizservice.dto.response.QuizChangeResponseDto;

import java.io.IOException;

public interface QuizExportStrategy {
    byte[] exportQuiz(QuizChangeResponseDto quizResponseDto) throws IOException;

}
