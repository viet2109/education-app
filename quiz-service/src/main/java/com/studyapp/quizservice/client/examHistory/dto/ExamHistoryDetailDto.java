package com.studyapp.quizservice.client.examHistory.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExamHistoryDetailDto {
    private Long id;
    private Long questionId;
    private Long answerId;
    private Boolean isCorrect;
}
