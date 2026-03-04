package com.bankapp.payment.client;

import com.bankapp.common.dto.UserDTO;
import com.bankapp.common.dto.TokenValidationRequest;
import com.bankapp.common.dto.TokenValidationResponse;
import com.bankapp.common.exception.ServiceUnavailableException;
import com.bankapp.common.interfaces.AuthServiceApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthServiceClient implements AuthServiceApi {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public AuthServiceClient(@Value("${services.auth.url}") String authServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public TokenValidationResponse validateToken(TokenValidationRequest request) {
        try {
            return restTemplate.postForObject(
                    authServiceUrl + "/api/auth/validate", request, TokenValidationResponse.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("auth-service", e);
        }
    }

    @Override
    public UserDTO getUserById(Long id) {
        try {
            return restTemplate.getForObject(
                    authServiceUrl + "/api/auth/users/" + id, UserDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("auth-service", e);
        }
    }
}
