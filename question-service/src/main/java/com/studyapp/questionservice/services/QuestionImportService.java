package com.studyapp.questionservice.services;

import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.services.QuestionImportStrategy.ExcelQuestionImportStrategy;
import com.studyapp.questionservice.services.QuestionImportStrategy.QuestionImportStrategy;
import com.studyapp.questionservice.services.QuestionImportStrategy.WordQuestionImportStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionImportService {
    private final Map<String, QuestionImportStrategy> strategies = new HashMap<>();

    public QuestionImportService() {
        //excel file
        strategies.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ExcelQuestionImportStrategy());

        //word file
        strategies.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", new WordQuestionImportStrategy());
    }

    public List<QuestionRequestDto> importQuestions(MultipartFile file, Long examId) {
        String fileType = file.getContentType();
        if (fileType == null || !strategies.containsKey(fileType)) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }

        QuestionImportStrategy strategy = strategies.get(fileType);
        return strategy.convertFileToDto(file, examId);
    }
}
