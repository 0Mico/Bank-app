package com.bankapp.auth.client;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Component
public class AccountServiceClient {

    private final RestClient restClient;

    @Value("${services.account.url}")
    private String accountServiceUrl;

    public AccountServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public AccountDTO createAccount(Long userId) {
        try {
            AccountDTO dto = new AccountDTO();
            dto.setUserId(userId);
            dto.setCurrency("EUR");

            return restClient.post()
                    .uri(accountServiceUrl + "/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .body(AccountDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("payment-service", e);
        }
    }

    public AccountDTO getAccountByIban(String iban) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/iban?iban=" + iban)
                    .retrieve()
                    .body(AccountDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("payment-service", e);
        }
    }

    public AccountDTO getAccountById(Long accountId) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/" + accountId)
                    .retrieve()
                    .body(AccountDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("payment-service", e);
        }
    }
}
