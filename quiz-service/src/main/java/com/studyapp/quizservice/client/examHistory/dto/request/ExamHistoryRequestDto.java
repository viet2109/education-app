package com.studyapp.quizservice.client.examHistory.dto.request;

import com.studyapp.quizservice.client.examHistory.dto.ExamHistoryDetailDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ExamHistoryRequestDto {
    LocalDateTime startedAt;
    LocalDateTime finishedAt;
    Double score;
    String userId;
    Long examId;
    List<ExamHistoryDetailDto> examHistoryDetail;
}
