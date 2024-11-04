package com.studyapp.quizservice.services;

import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.services.QuestionExportStrategy.ExcelQuizExportStrategy;
import com.studyapp.quizservice.services.QuestionExportStrategy.QuizExportStrategy;
import com.studyapp.quizservice.services.QuestionExportStrategy.WordQuizExportStrategy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class QuizExportService {
    private final Map<String, QuizExportStrategy> strategies = new HashMap<>();

    public QuizExportService() {
        //excel file
        strategies.put("excel", new ExcelQuizExportStrategy());

        //word file
        strategies.put("word", new WordQuizExportStrategy());
    }

    public byte[] exportQuiz(QuizResponseDto quizResponseDto, String fileType) {
        if (fileType == null || !strategies.containsKey(fileType)) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
        QuizExportStrategy strategy = strategies.get(fileType);
        return strategy.exportQuiz(quizResponseDto);
    }
}
