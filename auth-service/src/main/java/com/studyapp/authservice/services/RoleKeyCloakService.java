package com.studyapp.authservice.services;

import com.studyapp.authservice.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
public class RoleKeyCloakService {

    private final UserKeyCloakService userService;

    @Value("${app.keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;

    public void assignRole(String userId, Role roleName) {
        UserResource user = userService.getUser(userId);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName.name()).toRepresentation();
        user.roles().realmLevel().add(Collections.singletonList(representation));
    }
    
    public void deleteRoleFromUser(String userId, Role roleName) {
        UserResource user = userService.getUser(userId);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName.name()).toRepresentation();
        user.roles().realmLevel().remove(Collections.singletonList(representation));
    }

    private RolesResource getRolesResource(){
        return keycloak.realm(realm).roles();
    }

}
