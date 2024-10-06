package com.studyapp.userservice.dao;

import com.studyapp.userservice.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserDao extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhone(String phone);
}
