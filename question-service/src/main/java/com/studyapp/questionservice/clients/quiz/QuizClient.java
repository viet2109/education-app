package com.studyapp.questionservice.clients.quiz;

import com.studyapp.questionservice.clients.quiz.response.QuizResponseDto;
import com.studyapp.questionservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "quiz-service", path = "/quizzes", configuration = FeignConfig.class)
public interface QuizClient {

    @GetMapping("/{id}")
    ResponseEntity<QuizResponseDto> getExamById(@PathVariable Long id);
}
