package com.studyapp.questionservice.specification;
import com.studyapp.questionservice.entities.QuestionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class QuestionSpecification {

    public static Specification<QuestionEntity> hasExamId(Long examId) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("examId"), examId);
    }

    public static Specification<QuestionEntity> hasExamIds(List<Long> examIds) {
        return (root, query, criteriaBuilder) -> {
            if (examIds == null) return criteriaBuilder.conjunction();
            if (examIds.isEmpty()) {
                return criteriaBuilder.disjunction();  // Trả về điều kiện sai (false) nếu danh sách examIds rỗng
            }
            return root.get("examId").in(examIds);  // Dùng IN để tìm các bản ghi có examId nằm trong danh sách
        };
    }

    public static Specification<QuestionEntity> hasExcludeExamIds(List<Long> excludeExamIds) {
        return (root, query, criteriaBuilder) -> {
            if (excludeExamIds == null || excludeExamIds.isEmpty()) {
                return criteriaBuilder.conjunction(); // Không áp dụng điều kiện nếu danh sách excludeExamIds null hoặc rỗng
            }
            return criteriaBuilder.not(root.get("examId").in(excludeExamIds)); // NOT IN để loại trừ các examId trong danh sách
        };
    }


}

