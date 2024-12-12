package com.studyapp.questionservice.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Data
public class QuestionBankRequestDto {
    @Size(min = 1, message = "The questionIds must be at least one.")
    List<Long> questionIds;
    Long examId;
}
