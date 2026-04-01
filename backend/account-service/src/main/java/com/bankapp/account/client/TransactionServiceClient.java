package com.bankapp.account.client;

import com.common.dto.TransactionDTO;
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

    public TransactionDTO createTransaction(TransactionDTO dto) {
        try {
            return restClient.post()
                        .uri(transactionServiceUrl + "/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(dto)
                        .retrieve()
                        .body(TransactionDTO.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("transaction-service", e);
        }
    }
}
