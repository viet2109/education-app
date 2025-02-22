package com.studyapp.examhistoryservice.exception;

import com.studyapp.examhistoryservice.enums.ExamHistoryError;
import lombok.Getter;

@Getter
public class ExamHistoryException extends RuntimeException {
    private ExamHistoryError examHistoryError;

    public ExamHistoryException(ExamHistoryError examHistoryError) {
        super(examHistoryError.getMessage());
        this.examHistoryError = examHistoryError;
    }
}
