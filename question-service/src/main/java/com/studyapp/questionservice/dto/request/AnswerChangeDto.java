package com.studyapp.questionservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerChangeDto {

    @NotNull(message = "The answerId cannot be null")
    Long id;

    @NotBlank(message = "The answer content is mandatory")
    String content;

    @NotNull(message = "The correctness status cannot be null")
    Boolean isCorrect;
}
