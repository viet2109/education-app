package com.studyapp.questionservice.exceptions;

import com.studyapp.questionservice.enums.QuestionError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class QuestionException extends RuntimeException {
    QuestionError questionError;
    public QuestionException(QuestionError questionError) {
        super(questionError.getMessage());
        this.questionError = questionError;
    }
}
