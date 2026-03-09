package com.bankapp.common.interfaces;

import com.bankapp.common.dto.AccountDTO;
import java.util.List;
import java.math.BigDecimal;

public interface AccountServiceApi {
    AccountDTO getAccountById(Long accountId);
    AccountDTO getAccountByIban(String iban);
    List<AccountDTO> getAccountsByUserId(Long userId);
    void updateBalanceInternal(Long accountId, BigDecimal amountToAdd);
}
