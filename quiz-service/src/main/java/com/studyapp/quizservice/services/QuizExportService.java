package com.studyapp.quizservice.services;

import com.studyapp.quizservice.dto.response.QuizChangeResponseDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.services.QuizExportStrategy.ExcelQuizExportStrategy;
import com.studyapp.quizservice.services.QuizExportStrategy.QuizExportStrategy;
import com.studyapp.quizservice.services.QuizExportStrategy.WordQuizExportStrategy;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public byte[] exportQuiz(QuizChangeResponseDto quizResponseDto, String fileType) throws IOException {
        if (fileType == null || !strategies.containsKey(fileType)) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
        QuizExportStrategy strategy = strategies.get(fileType);
        return strategy.exportQuiz(quizResponseDto);
    }
}
