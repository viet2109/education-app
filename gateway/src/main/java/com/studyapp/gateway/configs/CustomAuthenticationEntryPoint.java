package com.studyapp.gateway.configs;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, org.springframework.security.core.AuthenticationException e) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Origin", "http://localhost:5173");
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Credentials", "true");

        String body = "{\"status\": \"Unauthorized\", \"errorMessage\": \"You are not authenticated.\"}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)))
                .doOnTerminate(exchange.getResponse()::setComplete);
    }
}

