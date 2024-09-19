package com.studyapp.questionservice.mapper;

import com.studyapp.questionservice.dto.QuestionDto;
import com.studyapp.questionservice.entities.QuestionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionEntity dtoToEntity(QuestionDto request);
    QuestionDto entityToDto(QuestionEntity entity);
}
