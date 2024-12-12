package com.studyapp.questionservice.clients.quiz;

import com.studyapp.questionservice.clients.quiz.response.QuizResponseDto;
import com.studyapp.questionservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FeignClient(value = "quiz-service", path = "/quizzes", configuration = FeignConfig.class)
public interface QuizClient {

    @GetMapping("/{id}")
    ResponseEntity<QuizResponseDto> getExamById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<Map<String, Object>> getQuizzesByQuery(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiratedAtAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiratedAtBefore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "true") boolean paged
    );
}
