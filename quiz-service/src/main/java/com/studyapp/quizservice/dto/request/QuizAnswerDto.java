package com.studyapp.quizservice.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuizAnswerDto {

    private Map<
            @NotNull(message = "Question ID cannot be null")
                    Long,

            List<
                    @NotNull(message = "Answer ID cannot be null")
                            Long
                    >
            > userAnswers;


    @NotBlank(message = "The userId is mandatory")
    private String userId;

    @NotNull(message = "The startedAt is mandatory")
    @PastOrPresent(message = "The startedAt must be in the past or present")
    private LocalDateTime startedAt;

    @NotNull(message = "The finishedAt is mandatory")
    private LocalDateTime finishedAt;

    @AssertTrue(message = "The finishedAt must be after startedAt")
    private boolean isFinishedAfterStart() {
        return startedAt != null && finishedAt != null && finishedAt.isAfter(startedAt);
    }
}

