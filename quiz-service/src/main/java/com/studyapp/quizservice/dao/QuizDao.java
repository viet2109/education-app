package com.studyapp.quizservice.dao;


import com.studyapp.quizservice.entities.QuizEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizDao extends CrudRepository<QuizEntity, Long> {

}
