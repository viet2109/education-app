package com.studyapp.questionservice.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum QuestionError {
    QUESTION_NOT_FOUND("Question not found", HttpStatus.NOT_FOUND),
    INVALID_QUESTION_ID("Invalid question ID", HttpStatus.BAD_REQUEST),
    QUESTION_ALREADY_EXISTS("Question already exists", HttpStatus.CONFLICT),
    NO_ANSWERS_PROVIDED("No answers provided for the question", HttpStatus.BAD_REQUEST),
    INVALID_ANSWER_FORMAT("Invalid answer format", HttpStatus.BAD_REQUEST),
    ANSWER_NOT_FOUND("Answer not found", HttpStatus.NOT_FOUND),
    EXAM_NOT_FOUND("Exam associated with the question not found", HttpStatus.NOT_FOUND),
    QUESTION_CREATION_FAILED("Failed to create question", HttpStatus.INTERNAL_SERVER_ERROR),
    QUESTION_UPDATE_FAILED("Failed to update question", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_QUESTION_ACCESS("Unauthorized access to the question", HttpStatus.UNAUTHORIZED);

    String message;
    HttpStatus httpStatus;
}
