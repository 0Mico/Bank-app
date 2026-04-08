package com.bankapp.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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
    public TransactionModel createTransaction(TransactionModel transaction) {
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

    @Override
    public TransactionModel getTransactionById(Long transactionId) {
        try {
            return restClient.get()
                .uri(transactionServiceUrl + transactionEndpoint + "/" + transactionId)
                .retrieve()
                .body(TransactionModel.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("transaction-service", e);        
        }
    }

    @Override
    public TransactionModel updateTransaction(Long id, TransactionModel transaction) {
        try {
            return restClient.put()
                .uri(transactionServiceUrl + transactionEndpoint + "/" + id)
                .body(transaction)
                .retrieve()
                .body(TransactionModel.class);
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("transaction-service", e);        
        }
    }

    @Override
    public void deleteTransaction(Long id) {
        try {
            restClient.delete()
                .uri(transactionServiceUrl + transactionEndpoint + "/" + id)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            throw ClientErrorMapper.handleException("transaction-service", e);        
        }
    }
}
