package com.bankapp.payment.client;

import com.bankapp.common.dto.UserDTO;
import com.bankapp.common.dto.TokenValidationRequest;
import com.bankapp.common.dto.TokenValidationResponse;
import com.bankapp.common.exception.ServiceUnavailableException;
import com.bankapp.common.interfaces.AuthServiceApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthServiceClient implements AuthServiceApi {

    private final RestClient restClient;
    private final String authServiceUrl;

    public AuthServiceClient(@Value("${services.auth.url}") String authServiceUrl, RestClient restClient) {
        this.restClient = restClient;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public TokenValidationResponse validateToken(TokenValidationRequest request) {
        try {
            return restClient.post()
                    .uri(authServiceUrl + "/api/auth/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(TokenValidationResponse.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("auth-service", e);
        }
    }

    @Override
    public UserDTO getUserById(Long id) {
        try {
            return restClient.get()
                    .uri(authServiceUrl + "/api/auth/users/" + id)
                    .retrieve()
                    .body(UserDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("auth-service", e);
        }
    }
}
