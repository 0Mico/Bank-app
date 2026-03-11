package com.bankapp.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.bankapp.common.dto.TransactionDTO;
import com.bankapp.common.exception.ServiceUnavailableException;
import com.bankapp.common.interfaces.TransactionServiceApi;

@Component
public class TransactionServiceClient implements TransactionServiceApi {

    private final RestClient restClient;
    private final String transactionServiceUrl;

    public TransactionServiceClient(@Value("${services.transaction.url}") String transactionServiceUrl, RestClient restClient) {
        this.restClient = restClient;
        this.transactionServiceUrl = transactionServiceUrl;
    }

    @Override
    public TransactionDTO createTransaction(TransactionDTO transaction) {
        try {
            return restClient.post()
                .uri(transactionServiceUrl + "/api/transactions")
                .body(transaction)
                .retrieve()
                .body(TransactionDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("transaction-service", e);
        }
    }

    @Override
    public TransactionDTO getTransactionById(Long transactionId) {
        try {
            return restClient.get()
                .uri(transactionServiceUrl + "/api/transactions/" + transactionId)
                .retrieve()
                .body(TransactionDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("transaction-service", e);
        }
    }

    @Override
    public TransactionDTO updateTransaction(Long id, TransactionDTO transaction) {
        try {
            return restClient.put()
                .uri(transactionServiceUrl + "/api/transactions/" + id)
                .body(transaction)
                .retrieve()
                .body(TransactionDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("transaction-service", e);        
        }
    }

    @Override
    public void deleteTransaction(Long id) {
        try {
            restClient.delete()
                .uri(transactionServiceUrl + "/api/transactions/" + id)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            throw new ServiceUnavailableException("transaction-service", e);        
        }
    }
}
