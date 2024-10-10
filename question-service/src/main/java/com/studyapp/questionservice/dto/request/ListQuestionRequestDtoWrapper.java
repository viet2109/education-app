package com.studyapp.questionservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListQuestionRequestDtoWrapper {
    @NotNull(message = "The list of questions cannot be null.")
    @Size(min = 1, message = "List of questions must contain at least one question.")
    private List<QuestionRequestDto> questions;
}
