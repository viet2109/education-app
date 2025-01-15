package com.studyapp.examhistoryservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExamHistoryError {
    EXAM_HISTORY_NOT_FOUND("The exam history was not found", HttpStatus.NOT_FOUND),
    EXAM_HISTORY_AlREADY_EXIST("The exam history has already exist", HttpStatus.CONFLICT),
    INVALID_EXAM_HISTORY_DATA("The data provided for the exam history is invalid", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELDS("Missing required fields in the exam history", HttpStatus.BAD_REQUEST),
    INVALID_DATE_RANGE("The finish date must be after the start date", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("The user associated with the exam history was not found", HttpStatus.NOT_FOUND),
    EXAM_NOT_FOUND("The exam associated with the exam history was not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS("Unauthorized access to the exam history", HttpStatus.UNAUTHORIZED),
    DATABASE_CONNECTION_FAILED("Failed to connect to the database", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_CONSTRAINT_VIOLATION("Data constraint violation occurred", HttpStatus.CONFLICT),
    EXCESSIVE_SCORE_VALUE("Score value exceeds the maximum allowed limit", HttpStatus.BAD_REQUEST),
    INCONSISTENT_DATA_STATE("Inconsistent data state detected for the exam history", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final HttpStatus httpStatus;

}

