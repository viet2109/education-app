package com.studyapp.questionservice.dao;

import com.studyapp.questionservice.entities.AnswerEntity;
import com.studyapp.questionservice.entities.QuestionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerDao extends CrudRepository<AnswerEntity, Long> {
}
