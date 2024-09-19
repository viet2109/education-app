package com.studyapp.questionservice.dao;

import com.studyapp.questionservice.entities.QuestionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionDao extends CrudRepository<QuestionEntity, Long> {
}
