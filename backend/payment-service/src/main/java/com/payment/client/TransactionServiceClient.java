package com.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.common.dto.TransactionDTO;
import com.common.model.TransactionModel;
import com.common.exception.ClientErrorMapper;
import com.common.interfaces.TransactionServiceApi;

@Component
public class TransactionServiceClient implements TransactionServiceApi {

    private final RestClient restClient;
    private final String transactionServiceUrl;
    private final String transactionEndpoint = "/api/transactions";

    public TransactionServiceClient(@Value("${services.transaction.url}") String transactionServiceUrl, 
                                    RestClient restClient) {
        this.restClient = restClient;
        this.transactionServiceUrl = transactionServiceUrl;
    }

    @Override
    public TransactionModel createTransaction(TransactionDTO transaction) {
        try {
            return restClient.post()
                .uri(transactionServiceUrl + transactionEndpoint)
                .body(transaction)
                .retrieve()
                .body(TransactionModel.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("transaction-service", e);        
        }
    }
}
