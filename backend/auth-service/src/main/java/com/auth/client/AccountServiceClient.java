package com.auth.client;

import com.common.dto.AccountDTO;
import com.common.exception.ClientErrorMapper;

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
            throw ClientErrorMapper.handleException("account-service", e);
        }
    }

    /**
     * Used to show the user name associated to the external account when clicking on a transaction
     * This call retrieve the account associated with the given iban
     */
    public AccountDTO getAccountByIban(String iban) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/iban?iban=" + iban)
                    .retrieve()
                    .body(AccountDTO.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("account-service", e);
        }
    }
}
