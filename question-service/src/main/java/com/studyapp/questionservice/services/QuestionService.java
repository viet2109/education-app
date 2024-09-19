package com.studyapp.questionservice.services;

import com.studyapp.questionservice.dao.QuestionDao;
import com.studyapp.questionservice.dto.QuestionDto;
import com.studyapp.questionservice.mapper.QuestionMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService {
    QuestionDao questionDao;
    QuestionMapper questionMapper;

    public void createQuestion(QuestionDto request) {
            questionDao.save(questionMapper.dtoToEntity(request));
    }

}
