package com.bankapp.payment.client;

import com.bankapp.common.dto.TransactionDTO;
import com.bankapp.common.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionServiceClient {

    private final RestTemplate restTemplate;
    private final String transactionServiceUrl;

    public TransactionServiceClient(@Value("${services.transaction.url}") String transactionServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.transactionServiceUrl = transactionServiceUrl;
    }

    public TransactionDTO createTransaction(TransactionDTO dto) {
        try {
            return restTemplate.postForObject(
                    transactionServiceUrl + "/api/transactions", dto, TransactionDTO.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("transaction-service", e);
        }
    }
}
