package com.account.client;

import com.common.model.TransactionModel;
import com.common.exception.ClientErrorMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TransactionServiceClient {

    private final RestClient restClient;
    private final String transactionServiceUrl;

    public TransactionServiceClient(@Value("${services.transaction.url}") String transactionServiceUrl,
                                    RestClient restClient) {
        this.restClient = restClient;
        this.transactionServiceUrl = transactionServiceUrl;
    }

    public TransactionModel createTransaction(TransactionModel dto) {
        try {
            return restClient.post()
                        .uri(transactionServiceUrl + "/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(dto)
                        .retrieve()
                        .body(TransactionModel.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("transaction-service", e);
        }
    }
}
