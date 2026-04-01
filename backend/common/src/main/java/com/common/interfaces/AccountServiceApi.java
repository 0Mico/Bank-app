package com.common.interfaces;

import java.util.List;

import com.common.dto.AccountDTO;

import java.math.BigDecimal;

public interface AccountServiceApi {
    AccountDTO getAccountById(Long accountId);
    AccountDTO getAccountByIban(String iban);
    List<AccountDTO> getAccountsByUserId(Long userId);
    void updateBalanceInternal(Long accountId, BigDecimal amountToAdd);
}
