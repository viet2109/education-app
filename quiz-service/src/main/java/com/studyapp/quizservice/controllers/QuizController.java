package com.studyapp.quizservice.controllers;

import com.studyapp.quizservice.client.examHistory.dto.response.ExamHistoryResponseDto;
import com.studyapp.quizservice.dto.request.QuizAnswerDto;
import com.studyapp.quizservice.dto.request.QuizRequestDto;
import com.studyapp.quizservice.dto.response.CategoryDto;
import com.studyapp.quizservice.dto.response.QuizChangeResponseDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.services.QuizExportService;
import com.studyapp.quizservice.services.QuizImportService;
import com.studyapp.quizservice.services.QuizService;
import com.studyapp.quizservice.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizController {
    QuizService quizService;
    QuizImportService quizImportService;
    QuizExportService quizExportService;

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponseDto> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getExamById(id));
    }

    @GetMapping("/settings/{id}")
    public ResponseEntity<QuizChangeResponseDto> getExamMangeById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getExamManageById(id));
    }

    @PostMapping
    public ResponseEntity<QuizResponseDto> createQuiz(@RequestBody QuizRequestDto requestDto) {
        QuizResponseDto responseDto = quizService.createExam(requestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuizById(id);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(quizService.getAllCategories());
    }

    @PostMapping("/{id}/score")
    public ResponseEntity<ExamHistoryResponseDto> calQuiz(@PathVariable Long id, @Valid @RequestBody QuizAnswerDto quizAnswerDto) {
        return ResponseEntity.ok(quizService.calculateScore(id, quizAnswerDto));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getQuizzesByQuery(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiratedAtAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiratedAtBefore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "true") boolean paged
    ) {
        try {
            Map<String, Object> response = new HashMap<>();

            if (paged) {
                // Xử lý phân trang
                List<Sort.Order> orders = new ArrayList<>();
                if (sort[0].contains(",")) {
                    for (String sortOrder : sort) {
                        String[] _sort = sortOrder.split(",");
                        orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                    }
                } else {
                    orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
                }

                Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
                Page<QuizResponseDto> pageQuizzes = quizService.getQuizzesByQuery(
                        title, category, createdBy, minDuration, maxDuration, expiratedAtAfter, expiratedAtBefore, pagingSort
                );

                Map<String, Object> pagination = new HashMap<>();
                pagination.put("currentPage", pageQuizzes.getNumber());
                pagination.put("pageSize", pageQuizzes.getSize());
                pagination.put("totalItems", pageQuizzes.getTotalElements());
                pagination.put("totalPages", pageQuizzes.getTotalPages());
                response.put("data", pageQuizzes.getContent());
                response.put("pagination", pagination);
            } else {
                // Không phân trang
                List<QuizResponseDto> quizzes = quizService.getQuizzesByQueryNoPagination(
                        title, category, createdBy, minDuration, maxDuration, expiratedAtAfter, expiratedAtBefore
                );
                response.put("data", quizzes);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Xử lý ngoại lệ
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getManageQuizzesByQuery(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiratedAtAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiratedAtBefore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        try {
            List<Sort.Order> orders = new ArrayList<>();

            // Xử lý các tham số sort
            if (sort[0].contains(",")) {
                // Nếu có nhiều trường để sắp xếp
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // Nếu chỉ có một trường để sắp xếp
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<QuizChangeResponseDto> pageQuizzes = quizService.getQuizzesManageByQuery(
                    title, category, createdBy, minDuration, maxDuration, expiratedAtAfter, expiratedAtBefore, pagingSort
            );

            List<QuizChangeResponseDto> quizzes = pageQuizzes.getContent();
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("currentPage", pageQuizzes.getNumber());
            pagination.put("pageSize", pageQuizzes.getSize());
            pagination.put("totalItems", pageQuizzes.getTotalElements());
            pagination.put("totalPages", pageQuizzes.getTotalPages());
            response.put("data", quizzes);
            response.put("pagination", pagination);


            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Xử lý ngoại lệ (có thể tùy chỉnh theo nhu cầu)
            return ResponseEntity.status(500).body(null);
        }
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    @PostMapping(value = "/import")
    public ResponseEntity<QuizResponseDto> createListQuestionByImport(@RequestPart MultipartFile file, HttpServletRequest request) {

        QuizResponseDto quizResponseDtos = quizImportService.importQuestions(file, JwtUtils.getUserIdFromToken(request));
        return ResponseEntity.ok().body(quizResponseDtos);
    }

    @GetMapping("/{examId}/export")
    public ResponseEntity<byte[]> exportQuestions(@PathVariable Long examId, @RequestParam(defaultValue = "word") String fileType) throws IOException {
        QuizChangeResponseDto quizResponseDto = quizService.getExamManageById(examId);

        // Kiểm tra nếu fileType null hoặc không đúng định dạng
        if (fileType == null || (!fileType.equals("word") && !fileType.equals("excel"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        byte[] content = quizExportService.exportQuiz(quizResponseDto, fileType);

        HttpHeaders headers = getHttpHeaders(fileType, quizResponseDto);

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }

    private HttpHeaders getHttpHeaders(String fileType, QuizChangeResponseDto quizResponseDto) {
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
