package com.studyapp.quizservice.exception;

import com.studyapp.quizservice.enums.QuizError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class QuizException extends RuntimeException {
    QuizError quizError;
    public QuizException(QuizError quizError) {
        super(quizError.getMessage());
        this.quizError = quizError;
    }
}
