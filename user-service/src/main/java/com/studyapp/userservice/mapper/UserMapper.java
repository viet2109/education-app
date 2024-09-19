package com.studyapp.userservice.mapper;

import com.studyapp.userservice.dto.request.UserRequest;
import com.studyapp.userservice.dto.response.UserResponse;
import com.studyapp.userservice.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity requestToEntity(UserRequest userRequest);
    UserResponse entityToResponse(UserEntity userEntity);
}
