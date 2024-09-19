package com.studyapp.userservice.services;

import com.studyapp.userservice.dao.UserDao;
import com.studyapp.userservice.dto.request.UserRequest;
import com.studyapp.userservice.dto.response.UserResponse;
import com.studyapp.userservice.entities.UserEntity;
import com.studyapp.userservice.enums.Role;
import com.studyapp.userservice.enums.UserError;
import com.studyapp.userservice.exceptions.UserException;
import com.studyapp.userservice.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {
    UserDao userDao;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        userDao.findByEmail(userRequest.getEmail()).ifPresent(user -> {
            throw new UserException(UserError.ALREADY_EXIST_EMAIL);
        });

        userDao.findByPhone(userRequest.getPhone()).ifPresent(user -> {
            throw new UserException(UserError.ALREADY_EXIST_PHONE);
        });

        UserEntity user = userMapper.requestToEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRoles(List.of(Role.USER));
        user.setActive(true);

        UserEntity userEntity = userDao.save(user);
        return userMapper.entityToResponse(userEntity);
    }

    public UserResponse getUserById(String id) {
        return userDao.findById(id)
                .map(userMapper::entityToResponse)
                .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
    }

    @Transactional
    public UserResponse updateUser(String id, UserRequest userRequest) {
        UserEntity existingUser = userDao.findById(id)
                .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));

        if (!Objects.equals(existingUser.getEmail(), userRequest.getEmail())) {
            userDao.findByEmail(userRequest.getEmail()).ifPresent(user -> {
                throw new UserException(UserError.ALREADY_EXIST_EMAIL);
            });
        }

        if (!Objects.equals(existingUser.getPhone(), userRequest.getPhone())) {
            userDao.findByPhone(userRequest.getPhone()).ifPresent(user -> {
                throw new UserException(UserError.ALREADY_EXIST_PHONE);
            });
        }

        existingUser.setEmail(userRequest.getEmail());
        existingUser.setName(userRequest.getName());
        existingUser.setPhone(userRequest.getPhone());

        if (!passwordEncoder.matches(userRequest.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        userDao.save(existingUser);
        return userMapper.entityToResponse(existingUser);
    }

    public List<UserResponse> getAllUser(boolean isActive) {
        return userDao.findAllByIsActive(isActive).stream()
                .map(userMapper::entityToResponse)
                .toList();
    }

    @Transactional
    public void deleteUser(String id) {
        userDao.findById(id).ifPresentOrElse(
                user -> userDao.deleteById(id),
                () -> { throw new UserException(UserError.USER_NOT_FOUND); }
        );
    }

    @Transactional
    public void deActiveUser(String id) {
        userDao.findById(id).ifPresentOrElse(user -> {
            if (user.isActive()) {
                user.setActive(false);
                userDao.save(user);
            }
        }, () -> {
            throw new UserException(UserError.USER_NOT_FOUND);
        });
    }
}

