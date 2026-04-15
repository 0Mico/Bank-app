package com.payment.client;

import com.common.exception.ClientErrorMapper;
import com.common.interfaces.AccountServiceApi;
import com.common.model.AccountModel;
import com.common.model.RecipientInfoModel;

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

    private final String accountService = "account-service";

    public AccountServiceClient(@Value("${services.account.url}") String accountServiceUrl, RestClient restClient) {
        this.restClient = restClient;
        this.accountServiceUrl = accountServiceUrl;
    }

    @Override
    public AccountModel getAccountById(Long accountId) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/" + accountId)
                    .retrieve()
                    .body(AccountModel.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException(accountService, e);
        }
    }

    @Override
    public AccountModel getAccountByIban(String iban) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/iban?iban=" + iban)
                    .retrieve()
                    .body(AccountModel.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException(accountService, e);
        }
    }

    @Override
    public List<AccountModel> getAccountsByUserId(Long userId) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/userId?userId=" + userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AccountModel>>() {});
        } catch (Exception e) {
            throw ClientErrorMapper.handleException(accountService, e);
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
            throw ClientErrorMapper.handleException(accountService, e);
        }
    }

    @Override
    public RecipientInfoModel analyzeRecipient(Long senderAccountId, String recipientIban) {
        try {
            return restClient.get()
                    .uri(accountServiceUrl + "/api/accounts/ownership-status?senderAccountId=" + senderAccountId + "&recipientIban=" + recipientIban)
                    .retrieve()
                    .body(RecipientInfoModel.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException(accountService, e);
        }
    }
}
