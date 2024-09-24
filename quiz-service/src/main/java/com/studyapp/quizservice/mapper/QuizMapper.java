package com.studyapp.quizservice.mapper;

import com.studyapp.quizservice.dto.request.QuizRequestDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.entities.QuizEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    QuizEntity rqDtoToEntity(QuizRequestDto dto);
    QuizResponseDto entityToRpDto(QuizEntity entity);
}
