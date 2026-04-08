package com.payment.client;

import com.common.dto.AccountDTO;
import com.common.exception.ClientErrorMapper;
import com.common.interfaces.AccountServiceApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;

import java.math.BigDecimal;
import java.util.List;

@Component
public class AccountServiceClient implements AccountServiceApi {

    private final RestClient restClient;
    private final String accountServiceUrl;

    public AccountServiceClient(@Value("${services.account.url}") String accountServiceUrl, RestClient restClient) {
        this.restClient = restClient;
        this.accountServiceUrl = accountServiceUrl;
    }

    @Override
    public AccountDTO getAccountById(Long accountId) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/" + accountId)
                    .retrieve()
                    .body(AccountDTO.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("account-service", e);
        }
    }

    @Override
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

    @Override
    public List<AccountDTO> getAccountsByUserId(Long userId) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/userId?userId=" + userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AccountDTO>>() {});
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("account-service", e);
        }
    }

    @Override
    public void updateBalanceInternal(Long accountId, BigDecimal amountToAdd) {
        try {
            restClient.put()
                    .uri(accountServiceUrl + "/api/accounts/internal/" + accountId + "/balance?amount=" + amountToAdd.toString())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("account-service", e);
        }
    }
}
