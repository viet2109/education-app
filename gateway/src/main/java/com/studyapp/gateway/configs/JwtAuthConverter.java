package com.studyapp.gateway.configs;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtAuthConverter implements Converter<Jwt, Mono<JwtAuthenticationToken>> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    @Value("${app.keycloak.admin.clientId}")
    private String clientId;

    @Override
    public Mono<JwtAuthenticationToken> convert(@Nonnull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        return Mono.just(new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt)));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        log.info("{}", jwt.getClaims());
        Collection<String> allRoles = new ArrayList<>();

        if (resourceAccess != null) {
            Map<String, Object> account = (Map<String, Object>) resourceAccess.get(clientId);
            if (account != null && account.containsKey("roles")) {
                Collection<String> resourceRoles = (Collection<String>) account.get("roles");
                allRoles.addAll(resourceRoles);
            }
        }

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
            allRoles.addAll(realmRoles);
        }

        // Log ra các vai trò đã trích xuất
        System.out.println("Extracted Roles: " + allRoles);

        return allRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

}
