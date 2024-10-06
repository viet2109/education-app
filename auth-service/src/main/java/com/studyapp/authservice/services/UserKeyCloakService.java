package com.studyapp.authservice.services;

import com.studyapp.authservice.client.CacheClient;
import com.studyapp.authservice.client.UserClient;
import com.studyapp.authservice.dto.request.UserLoginRequest;
import com.studyapp.authservice.dto.request.UserRequest;
import com.studyapp.authservice.dto.request.UserUpdateRequest;
import com.studyapp.authservice.dto.response.UserResponse;
import com.studyapp.authservice.enums.AuthError;
import com.studyapp.authservice.enums.Role;
import com.studyapp.authservice.exception.AuthException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserKeyCloakService {

    private final UserClient userClient;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.admin.clientId}")
    private String clientId;

    @Value("${app.keycloak.admin.clientSecret}")
    private String clientSecret;

    @Value("${app.keycloak.serverUrl}")
    private String serverUrl;

    private final Keycloak keycloak;
    private final CacheClient cacheClient;

    public UserResponse register(UserRequest userRequest) {
        UserResponse userResponse = userClient.createUser(userRequest).getBody();
        assert userResponse != null;
        UserRepresentation userRepresentation = getUserRepresentation(userRequest, userResponse);

        UsersResource usersResource = getUsersResource();

        try (Response response = usersResource.create(userRepresentation)) {
            HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
            log.info("status {}", response.getStatus());
            if (httpStatus.is4xxClientError()) {
                throw new AuthException(AuthError.USER_ALREADY_EXISTS);
            }
            log.info("You have created account successfully!!!");
        } catch (AuthException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AuthException(AuthError.KEYCLOAK_ERROR);
        }

        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(userRequest.getEmail(), true);
        UserRepresentation userRepresentation1 = userRepresentations.get(0);
        sendVerificationEmail(userRepresentation1.getId());
        assignDefaultRole(userRepresentation1.getId(), Role.USER);
        return userResponse;
    }

    private void assignDefaultRole(String userId, Role role) {
        RolesResource rolesResource = keycloak.realm(realm).roles();
        if (rolesResource.list().stream().anyMatch(roleRepresentation -> roleRepresentation.getName().equals(role.name()))) {
            RoleRepresentation representation = rolesResource.get(role.name()).toRepresentation();
            getUser(userId).roles().realmLevel().add(Collections.singletonList(representation));
        }
    }

    private UserRepresentation getUserRepresentation(UserRequest userRequest, UserResponse userResponse) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(userRequest.getName().split(" ")[0]);
        userRepresentation.setLastName(userRequest.getName().split(" ").length > 1 ? String.join(" ", Arrays.copyOfRange(userRequest.getName().split(" "), 1, userRequest.getName().split(" ").length)) : "");
        userRepresentation.setUsername(userRequest.getEmail());
        userRepresentation.setEmail(userRequest.getEmail());
        userRepresentation.setEnabled(true);

        // Add custom attribute
        userRepresentation.setAttributes(Map.of("db_id", List.of(userResponse.getId())));

        userRepresentation.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRequest.getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        return userRepresentation;
    }

    public void sendVerificationEmail(String userId) {
        UsersResource usersResource = getUsersResource();
        try {
            usersResource.get(userId).sendVerifyEmail();
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    public void deleteUser(String userId) {
        UsersResource usersResource = getUsersResource();
        Response response = usersResource.delete(userId);
        response.close();
    }

    public void forgotPassword(String username) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(username, true);
        if (userRepresentations.isEmpty()) throw new AuthException(AuthError.USER_NOT_FOUND);
        UserRepresentation userRepresentation = userRepresentations.get(0);
        UserResource userResource = usersResource.get(userRepresentation.getId());
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

    public void changePassword(String username) {

    }

    public void updateUserProfileAuth(String id, UserUpdateRequest userUpdateRequest) {
        // Tìm người dùng dựa trên thuộc tính "db_id"
        List<UserRepresentation> userRepresentations = getUsersResource().searchByAttributes("db_id");

        // Lọc ra người dùng với "db_id" khớp với tham số id
        UserRepresentation userRepresentation = userRepresentations.stream()
                .filter(user -> user.getAttributes() != null && user.getAttributes().get("db_id") != null
                        && user.getAttributes().get("db_id").stream().findFirst().orElse("").equals(id)) // Kiểm tra id
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthError.USER_NOT_FOUND));

        // Lấy UserResource để cập nhật người dùng
        UserResource userResource = getUsersResource().get(userRepresentation.getId());

        // Cập nhật các thuộc tính của người dùng dựa trên userUpdateRequest
        String[] nameParts = userUpdateRequest.getName().split(" ");
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length)) : "";

        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
        userRepresentation.setEmail(userUpdateRequest.getEmail());
        userRepresentation.setUsername(userUpdateRequest.getEmail());

        // Cập nhật lại thông tin người dùng
        userResource.update(userRepresentation);
    }


    public UserResource getUser(String userId) {
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

    public List<RoleRepresentation> getUserRoles(String userId) {
        return getUser(userId).roles().realmLevel().listAll();
    }

    public List<GroupRepresentation> getUserGroups(String userId) {
        return getUser(userId).groups();
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    public Map<String, Object> login(UserLoginRequest userLoginRequest) {
        Map<String, Object> responseMap = new HashMap<>();
        try (Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(userLoginRequest.getEmail())
                .password(userLoginRequest.getPassword())
                .grantType("password")
                .build()) {

            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            if (!isVerifiedEmail(userLoginRequest.getEmail())) throw new AuthException(AuthError.UNVERIFIED_EMAIL);
            if (!isActiveAccount(userLoginRequest.getEmail())) throw new AuthException(AuthError.DISABLED_ACCOUNT);
            String userId = getUsersResource().searchByEmail(userLoginRequest.getEmail(), true).get(0).getAttributes().get("db_id").get(0);
            UserResponse userResponse = userClient.findUserById(userId).getBody();

            cacheClient.saveRefreshToken(userId, tokenResponse.getRefreshToken());
            log.info(tokenResponse.getRefreshToken());

            responseMap.put("access_token", tokenResponse.getToken());
            responseMap.put("user", userResponse);
        } catch (AuthException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS);
        }
        return responseMap;
    }


    private boolean isVerifiedEmail(String email) {
        return getUsersResource().searchByEmail(email, true).stream().anyMatch(AbstractUserRepresentation::isEmailVerified);
    }

    private boolean isActiveAccount(String email) {
        return getUsersResource().searchByEmail(email, true).stream().anyMatch(UserRepresentation::isEnabled);
    }

    public void deActiveUser(String id) {
        // Tìm người dùng dựa trên thuộc tính "db_id"
        List<UserRepresentation> userRepresentations = getUsersResource().searchByAttributes("db_id");

        // Lọc ra người dùng với "db_id" khớp với tham số id
        UserRepresentation userRepresentation = userRepresentations.stream()
                .filter(user -> user.getAttributes() != null && user.getAttributes().get("db_id") != null
                        && user.getAttributes().get("db_id").stream().findFirst().orElse("").equals(id)) // Kiểm tra id
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthError.USER_NOT_FOUND));

        // Lấy UserResource để cập nhật người dùng
        UserResource userResource = getUser(userRepresentation.getId());
        userRepresentation.setEnabled(false);
        userResource.update(userRepresentation);
    }

    public void activeUser(String id) {
        // Tìm người dùng dựa trên thuộc tính "db_id"
        List<UserRepresentation> userRepresentations = getUsersResource().searchByAttributes("db_id");

        // Lọc ra người dùng với "db_id" khớp với tham số id
        UserRepresentation userRepresentation = userRepresentations.stream()
                .filter(user -> user.getAttributes() != null && user.getAttributes().get("db_id") != null
                        && user.getAttributes().get("db_id").stream().findFirst().orElse("").equals(id)) // Kiểm tra id
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthError.USER_NOT_FOUND));

        // Lấy UserResource để cập nhật người dùng
        UserResource userResource = getUser(userRepresentation.getId());
        userRepresentation.setEnabled(true);
        userResource.update(userRepresentation);
    }

    public void logout(String userId) {
        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // Lấy refresh token từ token hiện tại (hoặc lưu từ trước)
        String refreshToken = cacheClient.getRefreshToken(userId).getBody();

        // Tạo body request để gọi API đăng xuất
        String requestBody = String.format("client_id=%s&client_secret=%s&refresh_token=%s",
                clientId, clientSecret, refreshToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        // Gọi API Keycloak để đăng xuất
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        cacheClient.deleteRefreshToken(userId);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to logout from Keycloak: " + responseEntity.getBody());
        }
    }

    public String refreshAccessToken(String userId) {
        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        String refreshToken = cacheClient.getRefreshToken(userId).getBody();
        log.info(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String requestBody = String.format("grant_type=refresh_token&client_id=%s&client_secret=%s&refresh_token=%s",
                clientId, clientSecret, refreshToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            JSONObject jsonObject = new JSONObject(responseEntity.getBody());

            cacheClient.saveRefreshToken(userId, jsonObject.getString("refresh_token"));

            return jsonObject.getString("access_token");
        } else {
            throw new RuntimeException(responseEntity.getBody());
        }
    }

}
