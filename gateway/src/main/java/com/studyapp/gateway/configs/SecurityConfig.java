package com.studyapp.gateway.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    final String[] freeResourceUrls = {"/api/v1/auth/**", "/api/v1/quizzes/categories"};
    final String[] adminResourceUrls = {"/api/v1/auth/users/deactivate/**", "/api/v1/auth/users/activate/**"};
    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
//                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                                .pathMatchers(HttpMethod.GET, "/api/v1/quizzes").permitAll()
//                                .pathMatchers("/api/v1/auth/users/*/logout").authenticated()
//                                .pathMatchers(adminResourceUrls).hasRole("ADMIN")
//                                .pathMatchers(freeResourceUrls).permitAll()
//                                .anyExchange().authenticated()
                                .anyExchange().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)).authenticationEntryPoint(customAuthenticationEntryPoint))
                .build();
    }
}