package com.studyapp.questionservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyapp.questionservice.clients.quiz.QuizClient;
import com.studyapp.questionservice.dto.request.*;
import com.studyapp.questionservice.dto.response.QuestionChangeResponseDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.mapper.QuestionMapper;
import com.studyapp.questionservice.services.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {
    QuestionService questionService;
    QuestionMapper questionMapper;
    ObjectMapper objectMapper;

    @PostMapping(value = "/bulk")
    public ResponseEntity<?> createListQuestion(@ModelAttribute @Valid ListWrapper listDto) {
        questionService.createListQuestion(listDto.getQuestions());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/list").build().toUri();

        return ResponseEntity.created(location).body("Questions created successfully");
    }

    @PostMapping(value = "/bank")
    public ResponseEntity<?> createByListBankQuestion(@RequestBody @Valid QuestionBankRequestDto questionBankRequestDto) {
        questionService.createByListQuestionBankId(questionBankRequestDto.getQuestionIds(), questionBankRequestDto.getExamId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/list").build().toUri();

        return ResponseEntity.created(location).body("Questions created successfully");
    }

    @PostMapping(value = "/feign/bulk", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createListQuestionByFeign(@RequestPart("questionRequestFeignDtoList") String questionRequestFeignDtoListJson,  // Nhận JSON dưới dạng chuỗi
                                                       @RequestParam(value = "questionFilesKey", required = false) List<String> questionFilesKey,
                                                       @RequestPart(value = "questionFiles", required = false) List<MultipartFile> questionFiles,
                                                       @RequestParam(value = "answerFilesKey", required = false) List<String> answerFilesKey,
                                                       @RequestPart(value = "answerFiles", required = false) List<MultipartFile> answerFiles, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        // Chuyển đổi JSON string thành danh sách QuestionRequestFeignDto
        List<QuestionRequestFeignDto> questionRequestFeignDtoList = objectMapper.readValue(questionRequestFeignDtoListJson, new TypeReference<>() {
        });
        log.info("feign import");

        Map<String, MultipartFile> questionFileMap = mapFilesWithKeys(questionFiles, questionFilesKey);
        Map<String, MultipartFile> answerFileMap = mapFilesWithKeys(answerFiles, answerFilesKey);

        // Chuyển DTO từ Feign sang DTO nội bộ
        List<QuestionRequestDto> questionRequestDtoList = convertFeignDtoToLocalDto(questionRequestFeignDtoList, questionFileMap, answerFileMap);
        log.info(questionRequestDtoList.toString());
        // Gọi service để xử lý danh sách câu hỏi
        questionService.createListQuestion(questionRequestDtoList);

        // Tạo đường dẫn tới resource mới tạo
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/list").build().toUri();

        return ResponseEntity.created(location).body("Questions created successfully");
    }

    // Phương thức chuyển đổi DTO từ Feign sang DTO nội bộ
    private List<QuestionRequestDto> convertFeignDtoToLocalDto(List<QuestionRequestFeignDto> questionRequestFeignDtoList, Map<String, MultipartFile> questionFiles, Map<String, MultipartFile> answerFiles) {
        List<QuestionRequestDto> result = questionRequestFeignDtoList.stream().map(questionMapper::feignDtoToLocalDto).collect(Collectors.toList());

        int questionIndex = 0;

        for (QuestionRequestFeignDto feignDto : questionRequestFeignDtoList) {
            QuestionRequestDto questionRequestDto = result.get(questionIndex);

            // Gán file cho câu hỏi
            if (questionRequestDto.getFiles() == null) {
                questionRequestDto.setFiles(new ArrayList<>());
            }
            for (String index : feignDto.getFilesIndex()) {
                questionRequestDto.getFiles().add(questionFiles.get(index));
            }
            int answerIndex = 0;

            // Gán file cho câu trả lời
            for (AnswerRequestFeignDto answerRequestFeignDto : feignDto.getListAnswer()) {
                AnswerRequestDto answerRequestDto = questionRequestDto.getListAnswer().get(answerIndex);
                if (answerRequestDto.getFiles() == null) {
                    answerRequestDto.setFiles(new ArrayList<>());
                }
                for (String index : answerRequestFeignDto.getFilesIndex()) {
                    answerRequestDto.getFiles().add(answerFiles.get(index));
                }
                answerIndex++;
            }

            questionIndex++;
        }

        return result;
    }

    @PostMapping
    public ResponseEntity<String> createQuestion(@ModelAttribute @Valid QuestionRequestDto dto) {
        log.info(dto.toString());
        QuestionResponseDto responseDto = questionService.createQuestion(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(responseDto.getId()).toUri();

        return ResponseEntity.created(location).body(String.format("Question with id %s created successfully", responseDto.getId()));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByQuery(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of examId must contain at least one examId.") List<Long> examIds) {
        return ResponseEntity.ok(questionService.getQuestionsByQuery(examIds));
    }

    @GetMapping("/bank")
    public ResponseEntity<Map<String, Object>> getQuestionsBankByQuery(@RequestParam(required = false) List<String> category, @RequestParam(required = false) String createdBy, @RequestParam(required = false) List<Long> excludeExamIds, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size, @RequestParam(defaultValue = "id,asc") String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        // Call service to get paginated list of questions
        Page<QuestionResponseDto> result = questionService.getQuestionsBankByQuery(category, createdBy, excludeExamIds, pageable);

        // Prepare response data
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> pagination = new HashMap<>();

        response.put("data", result.getContent());
        pagination.put("currentPage", result.getNumber());
        pagination.put("pageSize", result.getSize());
        pagination.put("totalItems", result.getTotalElements());
        pagination.put("totalPages", result.getTotalPages());
        response.put("pagination", pagination);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/settings")
    public ResponseEntity<List<QuestionChangeResponseDto>> getQuestionsManageByQuery(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of examId must contain at least one examId.") List<Long> examIds) {
        return ResponseEntity.ok(questionService.getQuestionsManageByQuery(examIds));
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
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
    public ResponseEntity<?> deleteQuestionByIdsOrExamId(@RequestParam(required = false) @Valid @Size(min = 1, message = "List of questionsId must contain at least one questionId.") List<Long> ids, @RequestParam(required = false) Long examId) {
        questionService.deleteQuestionsByIdsOrExamId(ids, examId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }


    public Map<String, MultipartFile> mapFilesWithKeys(List<MultipartFile> files, List<String> keys) {
        Map<String, MultipartFile> fileMap = new HashMap<>();
        if (files != null && keys != null && files.size() == keys.size()) {
            for (int i = 0; i < files.size(); i++) {
                fileMap.put(keys.get(i), files.get(i));
            }
        }
        return fileMap;
    }

}
