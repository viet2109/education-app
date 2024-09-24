package com.studyapp.quizservice.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum QuizError {
    EXAM_NOT_FOUND("The exam associated with the question was not found", HttpStatus.NOT_FOUND),
    ;

    String message;
    HttpStatus httpStatus;
}
