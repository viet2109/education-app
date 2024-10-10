package com.studyapp.questionservice.services;

import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.services.QuestionExportStrategy.ExcelQuestionExportStrategy;
import com.studyapp.questionservice.services.QuestionExportStrategy.QuestionExportStrategy;
import com.studyapp.questionservice.services.QuestionExportStrategy.WordQuestionExportStrategy;
import com.studyapp.questionservice.services.QuestionImportStrategy.ExcelQuestionImportStrategy;
import com.studyapp.questionservice.services.QuestionImportStrategy.QuestionImportStrategy;
import com.studyapp.questionservice.services.QuestionImportStrategy.WordQuestionImportStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionExportService {
    private final Map<String, QuestionExportStrategy> strategies = new HashMap<>();

    public QuestionExportService() {
        //excel file
        strategies.put("excel", new ExcelQuestionExportStrategy());

        //word file
        strategies.put("word", new WordQuestionExportStrategy());
    }

    public byte[] exportQuestions(List<QuestionResponseDto> questionResponseDtos, String fileType) {
        if (fileType == null || !strategies.containsKey(fileType)) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
        QuestionExportStrategy strategy = strategies.get(fileType);
        return strategy.exportQuestions(questionResponseDtos);
    }
}
