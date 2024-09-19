package com.studyapp.questionservice.controllers;

import com.studyapp.questionservice.dto.QuestionDto;
import com.studyapp.questionservice.services.QuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {
    QuestionService questionService;
    public void createQuestion(@RequestBody QuestionDto dto) {
        questionService.createQuestion(dto);
    }
}
