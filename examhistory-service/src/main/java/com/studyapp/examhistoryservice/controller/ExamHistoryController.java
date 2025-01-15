package com.studyapp.examhistoryservice.controller;

import com.studyapp.examhistoryservice.dto.ExamHistoryResponseDto;
import com.studyapp.examhistoryservice.entity.ExamHistory;
import com.studyapp.examhistoryservice.service.ExamHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/exam-histories")
@RequiredArgsConstructor
public class ExamHistoryController {

    private final ExamHistoryService examHistoryService;

    @GetMapping("/{id}")
    public ResponseEntity<ExamHistoryResponseDto> findExamHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(examHistoryService.findById(id));
    }


    @GetMapping
    public ResponseEntity<Page<ExamHistoryResponseDto>> findExamHistories(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "startedAt,desc") String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        System.out.println(Arrays.toString(sort));

        if (sort[0].contains(",")) {

            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");

                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }

        Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
        return ResponseEntity.ok(examHistoryService.findByQueries(userId, pagingSort));
    }

    @PostMapping
    public ResponseEntity<ExamHistory> createExamHistory(@RequestBody ExamHistory examHistory) {
        ExamHistory savedExamHistory = examHistoryService.createExamHistory(examHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExamHistory);
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.trim().equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }
}
