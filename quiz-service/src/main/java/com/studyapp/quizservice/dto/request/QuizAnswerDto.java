package com.studyapp.quizservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuizAnswerDto {
    @NotNull(message = "The title is mandatory")
    private Long questionId;

    @Size(min = 1, message = "The answer size must be at least 1")
    private List<Long> answer;
}
