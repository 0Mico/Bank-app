package com.account.service;

import com.account.factory.AccountFactory;
import com.account.service.baseservice.BaseAccountService;
import com.common.dto.AccountDTO;
import com.common.dto.TransactionDTO;
import com.common.exception.BadRequestException;
import com.common.exception.ResourceNotFoundException;
import com.common.model.RecipientInfoModel;
import com.account.client.TransactionServiceClient;
import com.account.entity.Account;
import com.account.mapper.TransactionMapper;
import com.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AccountService implements BaseAccountService {

    private final AccountRepository accountRepository;
    private final TransactionServiceClient transactionClient;
    private final TransactionMapper transactionMapper;
    private final AccountFactory accountFactory;
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public AccountRepository getRepository() {
        return this.accountRepository;
    }

    @Override
    public Account createAccount(AccountDTO dto) {
        Account account = accountFactory.create(dto);
        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findAllByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Didn't find any account associated to the user"));
    }

    @Override
    public Account getAccountEntityById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }

    @Override
    public Account getAccountEntityByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new ResourceNotFoundException("Account with IBAN " + iban + " not found"));
    }

    @Override
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Deposit amount must be positive");
        }
        Account account = getAccountEntityById(accountId);
        account.setBalance(account.getBalance().add(amount));
        account = accountRepository.save(account);

        // Record the deposit as a transaction
        try {
            TransactionDTO depositTxn = transactionMapper.toDepositTransactionDto(account, amount);
            transactionClient.createTransaction(depositTxn);
        } catch (Exception e) {
            logger.warning("Failed to register deposit to " + getAccountEntityById(accountId).getIban());
        }
        return account;
    }

    @Override
    @Transactional
    public void updateBalanceInternal(Long accountId, BigDecimal amountToAdd) {
        Account account = getAccountEntityById(accountId);
        account.setBalance(account.getBalance().add(amountToAdd));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account updateAccountName(Long accountId, String name) {
        Account account = getAccountEntityById(accountId);
        account.setName(name);
        return accountRepository.save(account);
    }

    @Override
    public RecipientInfoModel determineOwnershipStatus(Long senderAccountId, String recipientIban) {
        Account senderAccount = getAccountEntityById(senderAccountId);
        List<Account> userAccounts = accountRepository.findAllByUserId(senderAccount.getUserId())
            .orElse(List.of());

        Account matchingAccount = userAccounts.stream()
            .filter(acc -> acc.getIban().equals(recipientIban))
            .findFirst()
            .orElse(null);

        if (matchingAccount != null) {
            String name = (matchingAccount.getName() != null && !matchingAccount.getName().isEmpty()) 
                ? matchingAccount.getName() 
                : matchingAccount.getIban();
            return new RecipientInfoModel("INTERNAL", name);
        } else {
            return new RecipientInfoModel("EXTERNAL", null);
        }
    }

    @Override
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        accountRepository.delete(account);
    }
}
