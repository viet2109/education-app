package com.studyapp.examhistoryservice.specification;

import com.studyapp.examhistoryservice.entity.ExamHistory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ExamHistorySpecification {

    public static Specification<ExamHistory> hasUserId(String userId) {
        return (Root<ExamHistory> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> builder.equal(root.get("userId"), userId);
    }

    public static Specification<ExamHistory> hasExamId(Long examId) {
        return (Root<ExamHistory> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> builder.equal(root.get("examId"), examId);
    }

    public static Specification<ExamHistory> startedAfter(LocalDateTime startDate) {
        return (Root<ExamHistory> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> builder.greaterThanOrEqualTo(root.get("startedAt"), startDate);
    }

    public static Specification<ExamHistory> startedBefore(LocalDateTime endDate) {
        return (Root<ExamHistory> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> builder.lessThanOrEqualTo(root.get("startedAt"), endDate);
    }

    public static Specification<ExamHistory> hasScoreGreaterThan(Double score) {
        return (Root<ExamHistory> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> builder.greaterThanOrEqualTo(root.get("score"), score);
    }

    public static Specification<ExamHistory> hasScoreLessThan(Double score) {
        return (Root<ExamHistory> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> builder.lessThanOrEqualTo(root.get("score"), score);
    }
}

