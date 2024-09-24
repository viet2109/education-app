package com.studyapp.questionservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChangeDto {

    @NotNull(message = "The questionId cannot be null")
    Long id;

    @NotBlank(message = "The question content is mandatory")
    String content;

    @NotNull(message = "The answer list cannot be null")
    @Size(min = 2, message = "There must be at least two answer")
    List<AnswerChangeDto> listAnswer;
}
