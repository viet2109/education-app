package com.studyapp.quizservice.dto.request;

import com.studyapp.quizservice.client.question.dto.request.QuestionRequestDto;
import com.studyapp.quizservice.enums.Category;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public class QuizImportRequestDto {
    @NotBlank(message = "The title is mandatory")
    String title;

    Category category;

    @Min(value = 60, message = "The duration must be at least 1 minute")
    Integer duration;

    @Future(message = "The expiration date must be in the future")
    LocalDateTime expiratedAt;

    @NotBlank(message = "The title is mandatory")
    String createdBy;

    @Size(min = 1, message = "List of question must contain at least one.")
    List<QuestionRequestDto> questionRequestDtos;
}
