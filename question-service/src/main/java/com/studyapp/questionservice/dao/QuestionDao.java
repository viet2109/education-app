package com.studyapp.questionservice.dao;

import com.studyapp.questionservice.entities.QuestionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends CrudRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByExamId(Long examId);
    @Query("SELECT q FROM QuestionEntity q WHERE "
            + "(:ids IS NULL OR q.id IN :ids) OR "
            + "(:examIds IS NULL OR q.examId IN :examIds)")
    List<QuestionEntity> findByQuery(@Param("ids") List<Long> ids, @Param("examIds") List<Long> examIds);

    @Modifying
    @Query("DELETE FROM QuestionEntity q WHERE "
            + "(:ids IS NULL OR q.id IN :ids) OR "
            + "(:examId IS NULL OR q.examId = :examId)")
    void deleteByIdsOrExamId(@Param("ids") List<Long> ids, @Param("examId") Long examId);

}
