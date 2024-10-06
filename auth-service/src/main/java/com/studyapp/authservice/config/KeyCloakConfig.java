package com.studyapp.authservice.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class KeyCloakConfig {

    @Value("${app.keycloak.admin.clientId}")
    private String clientId;
    @Value("${app.keycloak.admin.clientSecret}")
    private String clientSecret;
    @Value("${app.keycloak.realm}")
    private String realm;
    @Value("${app.keycloak.serverUrl}")
    private String serverUrl;


    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .clientSecret(clientSecret)
                .clientId(clientId)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .serverUrl(serverUrl)
                .build();
    }
}
