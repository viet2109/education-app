package com.studyapp.questionservice.mapper;

import com.studyapp.questionservice.dto.request.AnswerRequestDto;
import com.studyapp.questionservice.dto.response.AnswerResponseDto;
import com.studyapp.questionservice.entities.AnswerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    AnswerEntity rqDtoToEntity(AnswerRequestDto answerDto);
    AnswerResponseDto entityToRpDto(AnswerEntity answerEntity);
}
