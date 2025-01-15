package com.studyapp.examhistoryservice.service;

import com.studyapp.examhistoryservice.client.quiz.QuizClient;
import com.studyapp.examhistoryservice.dao.ExamHistoryDao;
import com.studyapp.examhistoryservice.dto.ExamHistoryResponseDto;
import com.studyapp.examhistoryservice.entity.ExamHistory;
import com.studyapp.examhistoryservice.enums.ExamHistoryError;
import com.studyapp.examhistoryservice.exception.ExamHistoryException;
import com.studyapp.examhistoryservice.mapper.ExamHistoryMapper;
import com.studyapp.examhistoryservice.specification.ExamHistorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamHistoryService {
    private final ExamHistoryDao examHistoryDao;
    private final ExamHistoryMapper examHistoryMapper;
    private final QuizClient quizClient;

    public ExamHistoryResponseDto findById(Long id) {
        ExamHistory examHistory = examHistoryDao.findById(id).orElseThrow(() -> new ExamHistoryException(ExamHistoryError.EXAM_HISTORY_NOT_FOUND));
        ExamHistoryResponseDto examHistoryResponseDto = examHistoryMapper.entityToDto(examHistory);
        examHistoryResponseDto.setExam(quizClient.getExamMangeById(examHistory.getExamId()).getBody());
        return examHistoryResponseDto;
    }

    public Page<ExamHistoryResponseDto> findByQueries(String userId, Pageable pageable) {
        Specification<ExamHistory> spec = Specification.where(null);
        if (StringUtils.hasText(userId)) {
            spec = spec.and(ExamHistorySpecification.hasUserId(userId));
        }
        return examHistoryDao.findAll(spec, pageable).map(entity -> {
            ExamHistoryResponseDto examHistoryResponseDto = examHistoryMapper.entityToDto(entity);
            examHistoryResponseDto.setExam(quizClient.getExamMangeById(entity.getExamId()).getBody());
            return examHistoryResponseDto;
        });
    }

    public ExamHistory createExamHistory(ExamHistory examHistory) {
        if (examHistory.getStartedAt() == null || examHistory.getFinishedAt() == null) {
            throw new IllegalArgumentException("Start and finish times cannot be null");
        }
        if (examHistory.getFinishedAt().isBefore(examHistory.getStartedAt())) {
            throw new IllegalArgumentException("Finish time must be after start time");
        }

        examHistory.getExamHistoryDetail().forEach(detail -> detail.setExamHistory(examHistory));
        return examHistoryDao.save(examHistory);
    }
}
