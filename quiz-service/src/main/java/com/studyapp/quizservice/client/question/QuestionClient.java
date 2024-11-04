package com.studyapp.quizservice.client.question;

import com.studyapp.quizservice.client.question.dto.request.QuestionRequestFeignDto;
import com.studyapp.quizservice.client.question.dto.response.QuestionResponseDto;
import com.studyapp.quizservice.config.FeignConfig;
import com.studyapp.quizservice.config.FeignMultipartConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(value = "question-service", path = "/questions", configuration = {FeignMultipartConfig.class, FeignConfig.class})
public interface QuestionClient {


    @GetMapping
    ResponseEntity<List<QuestionResponseDto>> getQuestionsByQuery(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of examId must contain at least one examId.") List<Long> examIds);

    @DeleteMapping
    ResponseEntity<?> deleteQuestionByIdsOrExamId(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of questionsId must contain at least one questionId.") List<Long> ids,
                                                  @RequestParam(required = false) Long examId);

    @PostMapping(value = "/feign/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> createListQuestionByFeign(
            @RequestPart("questionRequestFeignDtoList") String questionRequestFeignDtoListJson,  // Dữ liệu JSON dưới dạng chuỗi
            @RequestPart(value = "questionFiles", required = false) List<MultipartFile> questionFiles,
            @RequestPart(value = "answerFiles", required = false) List<MultipartFile> answerFiles);
}


