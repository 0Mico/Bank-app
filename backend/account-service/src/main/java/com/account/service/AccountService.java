package com.account.service;

import com.account.factory.AccountFactory;
import com.account.factory.ConcreteAccountFactory;
import com.common.dto.AccountDTO;
import com.common.dto.TransactionDTO;
import com.common.dto.RecipientInfoDTO;
import com.common.exception.BadRequestException;
import com.common.exception.ResourceNotFoundException;
import com.account.client.TransactionServiceClient;
import com.account.entity.Account;
import com.account.mapper.TransactionMapper;
import com.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionServiceClient transactionClient;
    private final TransactionMapper transactionMapper;
    private final AccountFactory accountFactory;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public AccountService(AccountRepository accountRepository, TransactionServiceClient transactionClient, TransactionMapper transactionMapper, ConcreteAccountFactory accountFactory) {
        this.accountRepository = accountRepository;
        this.transactionClient = transactionClient;
        this.transactionMapper = transactionMapper;
        this.accountFactory = accountFactory;
    }

    public Account createAccount(AccountDTO dto) {
        Account account = accountFactory.create(dto);
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findAllByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Didn't find any account associated to the user"));
    }

    public Account getAccountEntityById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }

    public Account getAccountEntityByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new ResourceNotFoundException("Account with IBAN " + iban + " not found"));
    }

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

    @Transactional
    public void updateBalanceInternal(Long accountId, BigDecimal amountToAdd) {
        Account account = getAccountEntityById(accountId);
        account.setBalance(account.getBalance().add(amountToAdd));
        accountRepository.save(account);
    }

    @Transactional
    public Account updateAccountName(Long accountId, String name) {
        Account account = getAccountEntityById(accountId);
        account.setName(name);
        return accountRepository.save(account);
    }

    public RecipientInfoDTO determineOwnershipStatus(Long senderAccountId, String recipientIban) {
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
            return new RecipientInfoDTO("INTERNAL", name);
        } else {
            return new RecipientInfoDTO("EXTERNAL", null);
        }
    }

    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        accountRepository.delete(account);
    }
}
