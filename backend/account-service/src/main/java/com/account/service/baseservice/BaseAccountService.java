package com.account.service.baseservice;

import com.account.entity.Account;
import com.account.repository.AccountRepository;
import com.common.dto.AccountDTO;
import com.common.interfaces.BaseService;
import com.common.model.RecipientInfoModel;

import java.math.BigDecimal;
import java.util.List;

public interface BaseAccountService extends BaseService<AccountRepository, Account, Long> {

    Account createAccount(AccountDTO dto);
    List<Account> getAccountsByUserId(Long userId);
    Account getAccountEntityById(Long id);
    Account getAccountEntityByIban(String iban);
    Account deposit(Long accountId, BigDecimal amount);
    void updateBalanceInternal(Long accountId, BigDecimal amountToAdd);
    Account updateAccountName(Long accountId, String name);
    RecipientInfoModel determineOwnershipStatus(Long senderAccountId, String recipientIban);
    void deleteAccount(Long accountId);
}
