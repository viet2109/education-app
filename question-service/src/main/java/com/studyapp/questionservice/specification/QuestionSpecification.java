package com.studyapp.questionservice.specification;
import com.studyapp.questionservice.entities.QuestionEntity;
import org.springframework.data.jpa.domain.Specification;

public class QuestionSpecification {

    public static Specification<QuestionEntity> hasExamId(Long examId) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("examId"), examId);
    }
}

