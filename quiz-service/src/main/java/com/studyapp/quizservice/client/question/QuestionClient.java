package com.studyapp.quizservice.client.question;

import com.studyapp.quizservice.client.question.dto.response.QuestionResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "question-service", path = "/questions")
public interface QuestionClient {
    @GetMapping
    ResponseEntity<List<QuestionResponseDto>> getListQuestion(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of questionsId must contain at least one questionId.") List<Long> ids,
                                                              @RequestParam(required = false) @Valid @Size(min = 1, message = "List of examId must contain at least one examId.") List<Long> examIds);
}
