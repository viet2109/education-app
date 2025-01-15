package com.studyapp.examhistoryservice.mapper;

import com.studyapp.examhistoryservice.dto.ExamHistoryResponseDto;
import com.studyapp.examhistoryservice.entity.ExamHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExamHistoryMapper {
    ExamHistoryResponseDto entityToDto(ExamHistory examHistory);
}
