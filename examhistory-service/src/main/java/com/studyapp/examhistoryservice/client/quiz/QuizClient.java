package com.studyapp.examhistoryservice.client.quiz;

import com.studyapp.examhistoryservice.client.quiz.dto.Quiz;
import com.studyapp.examhistoryservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "quiz-service", path = "/quizzes", configuration = FeignConfig.class)
public interface QuizClient {
    @GetMapping("/settings/{id}")
    ResponseEntity<Quiz> getExamMangeById(@PathVariable Long id);
}
