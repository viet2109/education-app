package com.studyapp.questionservice.dao;

import com.studyapp.questionservice.entities.QuestionEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends CrudRepository<QuestionEntity, Long>, JpaSpecificationExecutor<QuestionEntity> {
    List<QuestionEntity> findByExamId(Long examId);
}
