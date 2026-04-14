package com.payment.utils;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.common.interfaces.AccountServiceApi;
import com.payment.client.AccountServiceClient;

@Component
public class MoneyTransferHelper {

    private final AccountServiceApi accountServiceClient;
    
    public MoneyTransferHelper(AccountServiceClient accountServiceClient) {
        this.accountServiceClient = accountServiceClient;
    }

    public void debitSender(Long fromAccountId, BigDecimal amount) {
        accountServiceClient.updateBalanceInternal(fromAccountId, amount.negate());
    }

    public void creditReceiver(Long toAccountId, BigDecimal amount) {
         accountServiceClient.updateBalanceInternal(toAccountId, amount);
    }
}
