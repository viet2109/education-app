package com.studyapp.questionservice.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ListWrapper {
    @Size(min = 1, message = "List of questions must contain at least one question.")
    private List<QuestionRequestDto> questions;
}
