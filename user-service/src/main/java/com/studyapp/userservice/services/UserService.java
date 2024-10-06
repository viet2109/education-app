package com.studyapp.userservice.services;

import com.studyapp.userservice.client.AuthClient;
import com.studyapp.userservice.client.UserUpdateAuthProfileRequest;
import com.studyapp.userservice.dao.UserDao;
import com.studyapp.userservice.dto.request.UserRequest;
import com.studyapp.userservice.dto.response.UserResponse;
import com.studyapp.userservice.entities.UserEntity;
import com.studyapp.userservice.enums.UserError;
import com.studyapp.userservice.exceptions.UserException;
import com.studyapp.userservice.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {
    UserDao userDao;
    UserMapper userMapper;
    AuthClient authClient;

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        userDao.findByEmail(userRequest.getEmail()).ifPresent(user -> {
            throw new UserException(UserError.ALREADY_EXIST_EMAIL);
        });

        userDao.findByPhone(userRequest.getPhone()).ifPresent(user -> {
            throw new UserException(UserError.ALREADY_EXIST_PHONE);
        });

        UserEntity user = userMapper.requestToEntity(userRequest);

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
        // Tìm kiếm người dùng theo ID
        UserEntity existingUser = userDao.findById(id)
                .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));

        String newEmail = userRequest.getEmail();
        String newPhone = userRequest.getPhone();
        String newName = userRequest.getName();
        UserUpdateAuthProfileRequest userUpdateAuthProfileRequest = UserUpdateAuthProfileRequest.builder().build();


        // Kiểm tra xem email đã tồn tại hay chưa
        if (!existingUser.getEmail().equals(newEmail)) {
            userDao.findByEmail(newEmail).ifPresent(user -> {
                throw new UserException(UserError.ALREADY_EXIST_EMAIL);
            });
            existingUser.setEmail(newEmail);
            userUpdateAuthProfileRequest.setEmail(newEmail);
        }

        // Kiểm tra xem số điện thoại đã tồn tại hay chưa
        if (!existingUser.getPhone().equals(newPhone)) {
            userDao.findByPhone(newPhone).ifPresent(user -> {
                throw new UserException(UserError.ALREADY_EXIST_PHONE);
            });
            existingUser.setPhone(newPhone);
        }

        // Chỉ cập nhật tên nếu tên mới khác với tên hiện tại
        if (!existingUser.getName().equals(newName)) {
            existingUser.setName(newName);
            userUpdateAuthProfileRequest.setName(newName);
        }
        authClient.updateUser(existingUser.getId(), userUpdateAuthProfileRequest);

        // Lưu lại thay đổi vào database
        userDao.save(existingUser);

        // Trả về phản hồi từ entity đã được cập nhật
        return userMapper.entityToResponse(existingUser);
    }


    public List<UserResponse> getAllUser() {
        return userDao.findAll().stream().map(userMapper::entityToResponse).toList();
    }

    @Transactional
    public void deleteUser(String id) {
        userDao.findById(id).ifPresentOrElse(
                user -> userDao.deleteById(id),
                () -> {
                    throw new UserException(UserError.USER_NOT_FOUND);
                }
        );
    }
}

