package com.studyapp.questionservice.dao;

import com.studyapp.questionservice.entities.QuestionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends CrudRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByExamId(Long examId);
    @Query("SELECT q FROM QuestionEntity q WHERE "
            + "(:ids IS NULL OR q.id IN :ids) AND "
            + "(:examIds IS NULL OR q.examId IN :examIds)")
    List<QuestionEntity> findByIdsAndExamIds(@Param("ids") List<Long> ids, @Param("examIds") List<Long> examIds);
}
