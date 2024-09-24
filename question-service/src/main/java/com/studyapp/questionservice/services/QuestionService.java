package com.studyapp.questionservice.services;

import com.studyapp.questionservice.clients.quiz.QuizClient;
import com.studyapp.questionservice.dao.AnswerDao;
import com.studyapp.questionservice.dao.QuestionDao;
import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.entities.QuestionEntity;
import com.studyapp.questionservice.enums.QuestionError;
import com.studyapp.questionservice.exceptions.QuestionException;
import com.studyapp.questionservice.mapper.AnswerMapper;
import com.studyapp.questionservice.mapper.QuestionMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService {
    QuestionDao questionDao;
    AnswerDao answerDao;
    QuestionMapper questionMapper;
    AnswerMapper answerMapper;
    QuizClient quizClient;

    @Transactional
    public List<QuestionResponseDto> createListQuestion(List<QuestionRequestDto> listDto) {
        listDto.forEach(question -> {
            quizClient.getExamById(question.getExamId());
        });
        List<QuestionEntity> listSavedQuestion = Streamable.of(questionDao.saveAll(listDto.stream().map(questionMapper::rqDtoToEntity_createEntity).toList())).toList();
        return listSavedQuestion.stream().map(questionMapper::entityToRpDto).toList();

    }

    @Transactional
    public QuestionResponseDto createQuestion(QuestionRequestDto questionDto) {
        return createListQuestion(List.of(questionDto)).getFirst();
    }

    @Transactional
    public void deleteListQuestion(List<Long> listQuestionId) {
        List<QuestionEntity> existingQuestions = Streamable.of(questionDao.findAllById(listQuestionId)).toList();

        if (existingQuestions.size() != listQuestionId.size()) {
            throw new QuestionException(QuestionError.QUESTION_NOT_FOUND);
        }

        questionDao.deleteAllById(listQuestionId);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        deleteListQuestion(List.of(questionId));
    }

    public List<QuestionResponseDto> getListQuestion(List<Long> ids, List<Long> examIds) {
        return questionDao.findByIdsAndExamIds(ids, examIds).stream().map(questionMapper::entityToRpDto).toList();
    }

    public QuestionResponseDto getQuestionById(Long id) {
        return questionMapper.entityToRpDto(questionDao.findById(id).orElseThrow(() -> new QuestionException(QuestionError.QUESTION_NOT_FOUND)));
    }

    public List<QuestionResponseDto> getListQuestionByListId(List<Long> id) {
        return Streamable.of(questionDao.findAllById(id)).toList().stream().map(questionMapper::entityToRpDto).toList();
    }
}
