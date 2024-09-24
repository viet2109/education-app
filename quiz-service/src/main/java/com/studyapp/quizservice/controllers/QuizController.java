package com.studyapp.quizservice.controllers;

import com.studyapp.quizservice.dto.request.QuizRequestDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.services.QuizService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizController {
    QuizService quizService;

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponseDto> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getExamById(id));
    }

    @GetMapping()
    public ResponseEntity<List<QuizResponseDto>> getExams() {
        return ResponseEntity.ok(quizService.getAllExam());
    }

    @PostMapping
    public ResponseEntity<QuizResponseDto> createQuiz(@RequestBody QuizRequestDto requestDto) {
        QuizResponseDto responseDto = quizService.createExam(requestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

}
