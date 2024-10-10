package com.studyapp.questionservice.controllers;

import com.studyapp.questionservice.clients.quiz.QuizClient;
import com.studyapp.questionservice.clients.quiz.response.QuizResponseDto;
import com.studyapp.questionservice.dto.request.ListQuestionRequestDtoWrapper;
import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.services.QuestionExportService;
import com.studyapp.questionservice.services.QuestionImportService;
import com.studyapp.questionservice.services.QuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {
    QuestionService questionService;
    QuestionImportService questionImportService;
    QuestionExportService questionExportService;
    QuizClient quizClient;

    @PostMapping("/bulk")
    public ResponseEntity<?> createListQuestion(
            @ModelAttribute @Valid ListQuestionRequestDtoWrapper listDto) {
        questionService.createListQuestion(listDto.getQuestions());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/list")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body("Questions created successfully");
    }

    @PostMapping
    public ResponseEntity<String> createQuestion(
            @ModelAttribute @Valid QuestionRequestDto dto) {
        QuestionResponseDto responseDto = questionService.createQuestion(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(String.format("Question with id %s created successfully", responseDto.getId()));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByQuery(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of questionsId must contain at least one questionId.") List<Long> ids,
                                                                         @RequestParam(required = false) @Valid @Size(min = 1, message = "List of examId must contain at least one examId.") List<Long> examIds) {
        return ResponseEntity.ok(questionService.getQuestionsByQuery(ids, examIds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

//    @PutMapping
//    public ResponseEntity<?> updateListQuestion(@RequestBody @Valid List<Que> listDto) {
//        questionService.createListQuestion(listDto);
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/list")
//                .build()
//                .toUri();
//        return ResponseEntity.created(location).build();
//    }

    @DeleteMapping()
    public ResponseEntity<?> deleteQuestionByIdsOrExamId(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of questionsId must contain at least one questionId.") List<Long> ids,
                                                         @RequestParam(required = false) Long examId) {
        questionService.deleteQuestionsByIdsOrExamId(ids, examId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import/{examId}")
    public ResponseEntity<?> createListQuestionByImport(@RequestPart MultipartFile file, @PathVariable Long examId) {
        List<QuestionRequestDto> questionRequestDtoList = questionImportService.importQuestions(file, examId);
        createListQuestion(ListQuestionRequestDtoWrapper.builder().questions(questionRequestDtoList).build());
        return ResponseEntity.ok("You have import file successfully");
    }

    @GetMapping("/export/{examId}")
    public ResponseEntity<byte[]> exportQuestions(@PathVariable Long examId, @RequestParam("fileType") String fileType) {
        QuizResponseDto quizResponseDto = quizClient.getExamById(examId).getBody();

        // Kiểm tra nếu quizResponseDto là null
        if (quizResponseDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Kiểm tra nếu fileType null hoặc không đúng định dạng
        if (fileType == null || (!fileType.equals("word") && !fileType.equals("excel"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<QuestionResponseDto> questions = quizResponseDto.getListQuestion();
        byte[] content = questionExportService.exportQuestions(questions, fileType);

        // Log kích thước thay vì nội dung file
        log.info("Exported file size: " + content.length);

        HttpHeaders headers = getHttpHeaders(fileType, quizResponseDto);

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }

    private HttpHeaders getHttpHeaders(String fileType, QuizResponseDto quizResponseDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Tên file không quá ngắn và cắt hợp lý
        String fileNamePrefix = quizResponseDto.getTitle().length() > 9 ? quizResponseDto.getTitle().substring(0, 9) + "..." : quizResponseDto.getTitle();

        Map<String, String> extensionMap = new HashMap<>(Map.of("word", "docx", "excel", "xlsx"));
        String extension = extensionMap.get(fileType);

        // Kiểm tra extension null
        if (extension == null) {
            extension = "txt"; // fallback
        }

        String fileName = String.format("%s.%s", fileNamePrefix, extension);
        headers.setContentDispositionFormData("attachment", fileName);

        return headers;
    }

}
