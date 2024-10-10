package com.studyapp.questionservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequestDto {
    @NotBlank(message = "The answer content is mandatory")
    String content;

    @NotNull(message = "The correctness status cannot be null")
    Boolean isCorrect;

    List<MultipartFile> files;
}
