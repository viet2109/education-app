package com.studyapp.questionservice.services;

import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.services.QuizImportStrategy.ExcelQuestionImportStrategy;
import com.studyapp.questionservice.services.QuizImportStrategy.QuestionImportStrategy;
import com.studyapp.questionservice.services.QuizImportStrategy.WordQuestionImportStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionImportService {
    private final WordQuestionImportStrategy wordQuestionImportStrategy;
    private final ExcelQuestionImportStrategy excelQuestionImportStrategy;
    private final Map<String, QuestionImportStrategy> strategies = new HashMap<>();

    @PostConstruct
    public void initStrategies() {
        strategies.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelQuestionImportStrategy);
        strategies.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", wordQuestionImportStrategy);
    }

    public List<QuestionResponseDto> importQuestions(MultipartFile file, Long examId) {
        String fileType = file.getContentType();
        if (fileType == null || !strategies.containsKey(fileType)) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }

        QuestionImportStrategy strategy = strategies.get(fileType);
        return strategy.convertFileToDto(file, examId);
    }
}
