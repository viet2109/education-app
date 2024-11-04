package com.studyapp.quizservice.services;

import com.studyapp.quizservice.dto.request.QuizImportRequestDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.services.QuizImportStrategy.ExcelQuizImportStrategy;
import com.studyapp.quizservice.services.QuizImportStrategy.QuizImportStrategy;
import com.studyapp.quizservice.services.QuizImportStrategy.WordQuizImportStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizImportService {
    private final WordQuizImportStrategy wordQuizImportStrategy;
    private final ExcelQuizImportStrategy excelQuizImportStrategy;
    private final Map<String, QuizImportStrategy> strategies = new HashMap<>();

    @PostConstruct
    public void initStrategies() {
        strategies.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelQuizImportStrategy);
        strategies.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", wordQuizImportStrategy);
    }

    public QuizResponseDto importQuestions(MultipartFile file, String userId) {
        String fileType = file.getContentType();
        if (fileType == null || !strategies.containsKey(fileType)) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }

        QuizImportStrategy strategy = strategies.get(fileType);
        return strategy.convertFileToDto(file, userId);
    }
}
