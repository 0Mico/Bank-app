package com.common.interfaces;

import java.util.List;

import com.common.dto.RecipientInfoDTO;
import com.common.model.AccountModel;

import java.math.BigDecimal;

public interface AccountServiceApi {
    AccountModel getAccountById(Long accountId);
    AccountModel getAccountByIban(String iban);
    List<AccountModel> getAccountsByUserId(Long userId);
    void updateBalanceInternal(Long accountId, BigDecimal amountToAdd);
    RecipientInfoDTO analyzeRecipient(Long senderAccountId, String recipientIban);
}
