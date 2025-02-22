package com.studyapp.examhistoryservice.dto;

import com.studyapp.examhistoryservice.client.quiz.dto.Quiz;
import com.studyapp.examhistoryservice.entity.ExamHistoryDetail;
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
    Quiz exam;
    List<ExamHistoryDetail> examHistoryDetail;
}
