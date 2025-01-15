package com.studyapp.quizservice.client.examHistory.dto.response;

import com.studyapp.quizservice.client.examHistory.dto.ExamHistoryDetailDto;
import com.studyapp.quizservice.dto.response.QuizChangeResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ExamHistoryResponseDto {
    Long id;
    LocalDateTime startedAt;
    LocalDateTime finishedAt;
    Double score;
    String userId;
    QuizChangeResponseDto exam;
    List<ExamHistoryDetailDto> examHistoryDetail;
}
