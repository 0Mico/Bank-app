package com.common.interfaces;

import java.util.List;

import com.common.model.AccountModel;
import com.common.model.RecipientInfoModel;

import java.math.BigDecimal;

public interface AccountServiceApi {
    AccountModel getAccountById(Long accountId);
    AccountModel getAccountByIban(String iban);
    List<AccountModel> getAccountsByUserId(Long userId);
    void updateBalanceInternal(Long accountId, BigDecimal amountToAdd);
    RecipientInfoModel analyzeRecipient(Long senderAccountId, String recipientIban);
}
