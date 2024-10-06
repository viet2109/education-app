package com.studyapp.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class GroupKeyCloakService {
    private final UserKeyCloakService userService;

    public void assignGroup(String userId, String groupId) {
        UserResource user = userService.getUser(userId);
        user.joinGroup(groupId);
    }

    public void deleteGroupFromUser(String userId, String groupId) {
        UserResource user = userService.getUser(userId);
        user.leaveGroup(groupId);
    }
}
