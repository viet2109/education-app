package com.studyapp.questionservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyapp.questionservice.clients.quiz.QuizClient;
import com.studyapp.questionservice.clients.quiz.response.QuizResponseDto;
import com.studyapp.questionservice.dto.request.*;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.mapper.QuestionMapper;
import com.studyapp.questionservice.services.QuestionExportService;
import com.studyapp.questionservice.services.QuestionImportService;
import com.studyapp.questionservice.services.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {
    QuestionService questionService;
    QuestionImportService questionImportService;
    QuizClient quizClient;
    QuestionExportService questionExportService;
    QuestionMapper questionMapper;
    ObjectMapper objectMapper;


    @PostMapping(value = "/bulk")
    public ResponseEntity<?> createListQuestion(
            @ModelAttribute @Valid ListWrapper listDto) {
        questionService.createListQuestion(listDto.getQuestions());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/list")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body("Questions created successfully");
    }

    @PostMapping(value = "/feign/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createListQuestionByFeign(
            @RequestPart("questionRequestFeignDtoList") String questionRequestFeignDtoListJson,  // Nhận JSON dưới dạng chuỗi
            @RequestPart(value = "questionFiles", required = false) List<MultipartFile> questionFiles,
            @RequestPart(value = "answerFiles", required = false) List<MultipartFile> answerFiles) throws JsonProcessingException {

        // Chuyển đổi JSON string thành danh sách QuestionRequestFeignDto
        List<QuestionRequestFeignDto> questionRequestFeignDtoList = objectMapper.readValue(questionRequestFeignDtoListJson, new TypeReference<List<QuestionRequestFeignDto>>() {
        });
        log.info("feign import");
        // Chuyển DTO từ Feign sang DTO nội bộ
        List<QuestionRequestDto> questionRequestDtoList = convertFeignDtoToLocalDto(questionRequestFeignDtoList, questionFiles, answerFiles);
        log.info(questionRequestDtoList.toString());
        // Gọi service để xử lý danh sách câu hỏi
        questionService.createListQuestion(questionRequestDtoList);

        // Tạo đường dẫn tới resource mới tạo
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/list")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body("Questions created successfully");
    }

    // Phương thức chuyển đổi DTO từ Feign sang DTO nội bộ
    private List<QuestionRequestDto> convertFeignDtoToLocalDto(List<QuestionRequestFeignDto> questionRequestFeignDtoList, List<MultipartFile> questionFiles, List<MultipartFile> answerFiles) {
        List<QuestionRequestDto> result = questionRequestFeignDtoList.stream()
                .map(questionMapper::feignDtoToLocalDto)
                .collect(Collectors.toList());

        int questionIndex = 0;

        for (QuestionRequestFeignDto feignDto : questionRequestFeignDtoList) {
            QuestionRequestDto questionRequestDto = result.get(questionIndex);

            // Gán file cho câu hỏi
            if (questionRequestDto.getFiles() == null) {
                questionRequestDto.setFiles(new ArrayList<>());
            }
            for (Long index : feignDto.getFilesIndex()) {
                questionRequestDto.getFiles().add(questionFiles.get(Math.toIntExact(index)));
            }
            int answerIndex = 0;

            // Gán file cho câu trả lời
            for (AnswerRequestFeignDto answerRequestFeignDto : feignDto.getListAnswer()) {
                AnswerRequestDto answerRequestDto = questionRequestDto.getListAnswer().get(answerIndex);
                if (answerRequestDto.getFiles() == null) {
                    answerRequestDto.setFiles(new ArrayList<>());
                }
                for (Long index : answerRequestFeignDto.getFilesIndex()) {
                    answerRequestDto.getFiles().add(answerFiles.get(Math.toIntExact(index)));
                }
                answerIndex++;
            }

            questionIndex++;
        }

        return result;
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
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByQuery(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of examId must contain at least one examId.") List<Long> examIds) {
        return ResponseEntity.ok(questionService.getQuestionsByQuery(examIds));
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
    public ResponseEntity<?> createListQuestionByImport(@RequestPart MultipartFile file, @RequestParam Long examId) {
        List<QuestionResponseDto> questionResponseDtos = questionImportService.importQuestions(file, examId);
        return ResponseEntity.ok().body(questionResponseDtos);
    }

    @GetMapping("/export/{examId}")
    public ResponseEntity<byte[]> exportQuestions(@PathVariable Long examId, @RequestParam("fileType") String fileType) {
        QuizResponseDto quizResponseDto = quizClient.getExamById(examId).getBody();

        // Kiểm tra nếu fileType null hoặc không đúng định dạng
        if (fileType == null || (!fileType.equals("word") && !fileType.equals("excel"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        assert quizResponseDto != null;
        byte[] content = questionExportService.exportQuestions(quizResponseDto.getListQuestion(), fileType);

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
