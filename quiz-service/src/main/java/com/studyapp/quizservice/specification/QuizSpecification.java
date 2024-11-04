package com.studyapp.quizservice.specification;
import com.studyapp.quizservice.entities.QuizEntity;
import com.studyapp.quizservice.enums.Category;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class QuizSpecification {

    public static Specification<QuizEntity> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<QuizEntity> hasAnyCategory(List<Category> categories) {
        return (root, query, criteriaBuilder) -> root.get("category").in(categories);
    }

    public static Specification<QuizEntity> createdBy(String createdBy) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("createdBy")), "%" + createdBy.toLowerCase() + "%");
    }

    public static Specification<QuizEntity> hasDurationGreaterThanOrEqual(Integer minDuration) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), minDuration);
    }

    public static Specification<QuizEntity> hasDurationLessThanOrEqual(Integer maxDuration) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("duration"), maxDuration);
    }

    public static Specification<QuizEntity> expiratedAtAfter(LocalDateTime expiratedAtAfter) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("expiratedAt"), expiratedAtAfter);
    }

    public static Specification<QuizEntity> expiratedAtBefore(LocalDateTime expiratedAtBefore) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("expiratedAt"), expiratedAtBefore);
    }

}

