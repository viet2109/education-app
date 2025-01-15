package com.studyapp.quizservice.client.examHistory;

import com.studyapp.quizservice.client.examHistory.dto.request.ExamHistoryRequestDto;
import com.studyapp.quizservice.client.examHistory.dto.response.ExamHistoryResponseDto;
import com.studyapp.quizservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "examhistory-service", path = "/exam-histories", configuration = FeignConfig.class)
public interface ExamHistoryClient {
    @PostMapping
    ResponseEntity<ExamHistoryResponseDto> createExamHistory(@RequestBody ExamHistoryRequestDto examHistory) ;
}
