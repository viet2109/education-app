package com.studyapp.questionservice.mapper;

import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.entities.QuestionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionResponseDto entityToRpDto(QuestionEntity entity);
}
