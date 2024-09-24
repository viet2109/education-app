package com.studyapp.questionservice.mapper;

import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.entities.AnswerEntity;
import com.studyapp.questionservice.entities.QuestionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionEntity rqDtoToEntity(QuestionRequestDto dto);

    default QuestionEntity rqDtoToEntity_createEntity(QuestionRequestDto dto) {
        if (dto == null) return null;
        QuestionEntity entity = QuestionEntity
                .builder()
                .content(dto.getContent())
                .listAnswer(null)
                .examId(dto.getExamId())
                .build();
        List<AnswerEntity> answerEntityList = dto.getListAnswer().stream().map(answerDto -> AnswerEntity
                .builder()
                .content(answerDto.getContent())
                .isCorrect(answerDto.getIsCorrect())
                .question(entity)
                .build()).toList();
        entity.setListAnswer(answerEntityList);
        return entity;
    }


    ;

    QuestionResponseDto entityToRpDto(QuestionEntity entity);
}
