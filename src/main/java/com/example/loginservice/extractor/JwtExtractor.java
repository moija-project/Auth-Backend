package com.example.loginservice.extractor;

import jakarta.servlet.http.HttpServletRequest;

public class JwtExtractor {

    public static String extractJwtToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }
}
