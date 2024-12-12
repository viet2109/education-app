package com.studyapp.questionservice.mapper;

import com.studyapp.questionservice.dto.request.QuestionChangeDto;
import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.request.QuestionRequestFeignDto;
import com.studyapp.questionservice.dto.response.QuestionChangeResponseDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.entities.QuestionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionResponseDto entityToRpDto(QuestionEntity entity);
    QuestionChangeResponseDto entityToChangeRpDto(QuestionEntity entity);
    QuestionRequestDto feignDtoToLocalDto(QuestionRequestFeignDto feignDto);
}
