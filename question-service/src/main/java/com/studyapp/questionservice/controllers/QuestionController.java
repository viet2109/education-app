package com.studyapp.questionservice.controllers;

import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.services.QuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {
    QuestionService questionService;

    @PostMapping("/bulk")
    public ResponseEntity<?> createListQuestion(
            @RequestBody @Valid @Size(min = 1, message = "List of questions must contain at least one question.") List<QuestionRequestDto> listDto) {
        questionService.createListQuestion(listDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/list")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body("Questions created successfully");
    }

    @PostMapping()
    public ResponseEntity<String> createQuestion(
            @RequestBody @Valid QuestionRequestDto dto) {
        QuestionResponseDto responseDto = questionService.createQuestion(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(String.format("Question with id %s created successfully", responseDto.getId()));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByQuery(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of questionsId must contain at least one questionId.") List<Long> ids,
                                            @RequestParam(required = false) @Valid @Size(min = 1, message = "List of examId must contain at least one examId.") List<Long> examIds) {
        return ResponseEntity.ok(questionService.getQuestionsByQuery(ids, examIds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

//    @PutMapping
//    public ResponseEntity<?> updateListQuestion(@RequestBody @Valid List<Que> listDto) {
//        questionService.createListQuestion(listDto);
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/list")
//                .build()
//                .toUri();
//        return ResponseEntity.created(location).build();
//    }

    @DeleteMapping()
    public ResponseEntity<?> deleteQuestionByIdsOrExamId(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of questionsId must contain at least one questionId.") List<Long> ids,
                                                @RequestParam(required = false) Long examId) {
        questionService.deleteQuestionsByIdsOrExamId(ids, examId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
