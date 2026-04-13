package com.account.factory;

import com.account.entity.Account;
import com.common.dto.AccountDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class ConcreteAccountFactory implements AccountFactory {

    private final Random random = new Random();

    private String createMockIban() {
        String bankCode = "NEXS";
        String countryCode = "IT";
        String checkDigits = String.format("%02d", random.nextInt(100));
        StringBuilder bban = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            bban.append(random.nextInt(10));
        }
        String iban = (countryCode + checkDigits + bankCode + bban.toString().substring(0, 18)); // Total 27 chars
        return iban;
    }

    @Override
    public Account create(AccountDTO dto) {
        Account account = new Account();
        account.setUserId(dto.getUserId());
        account.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        account.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "EUR");
        account.setIban(createMockIban());
        return account;
    }
}
