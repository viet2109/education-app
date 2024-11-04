package com.studyapp.quizservice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class JwtUtils {
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public static String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTH_HEADER);
        log.info("token: {}" ,bearerToken);
        // Kiểm tra nếu header chứa token với prefix "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            // Loại bỏ prefix và trả về token

            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public static String getUserIdFromToken(HttpServletRequest request) {
        String token = getJwtFromRequest(request);
        if (token != null) {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("profile").asString();
        }
        return null;
    }
}
