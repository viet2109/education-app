package com.studyapp.quizservice.dto.request;

import com.studyapp.quizservice.enums.Category;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizRequestDto {
    @NotBlank(message = "The title is mandatory")
    String title;

    @NotNull(message = "The category is mandatory")
    Category category;

    @Min(value = 60, message = "The duration must be at least 1 minute")
    Integer duration;

    @Future(message = "The expiration date must be in the future")
    LocalDateTime expiratedAt;

    @NotBlank(message = "The title is mandatory")
    String createdBy;
}
