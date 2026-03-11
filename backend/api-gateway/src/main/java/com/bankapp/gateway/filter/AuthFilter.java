package com.bankapp.gateway.filter;

import com.bankapp.common.dto.TokenValidationRequest;
import com.bankapp.common.dto.TokenValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final RestClient authClient;

    private final List<String> openEndpoints = List.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    public AuthFilter(@Value("${services.auth.url}") String authServiceUrl) {
        this.authClient = RestClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Skip auth for open endpoints and CORS preflight
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isOpenEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check if auth header and token are present
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"message\":\"Missing or invalid Authorization header\"}");
            return;
        }

        String token = authHeader.substring(7);
        String httpMethod = request.getMethod();

        // Send token to auth service for validation
        try {
            TokenValidationRequest validationRequest = new TokenValidationRequest(token, path, httpMethod);

            TokenValidationResponse validation = authClient.post()
                    .uri("/api/auth/validate")
                    .body(validationRequest)
                    .retrieve()
                    .body(TokenValidationResponse.class);

            if (validation != null && validation.isValid()) {
                request.setAttribute("X-User-Id", String.valueOf(validation.getUserId()));
                request.setAttribute("X-User-Email", validation.getEmail());
                request.setAttribute("X-User-Role", validation.getRole().name());
                chain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.getWriter().write("{\"message\":\"Auth service unavailable\"}");
        }
    }

    private boolean isOpenEndpoint(String path) {
        return openEndpoints.stream().anyMatch(path::startsWith);
    }
}
