package com.bankapp.payment.service;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.dto.TransactionDTO;
import com.bankapp.common.enums.TransactionCategory;
import com.bankapp.common.enums.TransactionType;
import com.bankapp.common.exception.BadRequestException;
import com.bankapp.common.exception.ResourceNotFoundException;
import com.bankapp.payment.client.TransactionServiceClient;
import com.bankapp.payment.entity.Account;
import com.bankapp.payment.repository.AccountRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.Random;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionServiceClient transactionClient;
    private final Random random = new Random();

    public AccountService(AccountRepository accountRepository,
            TransactionServiceClient transactionClient) {
        this.accountRepository = accountRepository;
        this.transactionClient = transactionClient;
    }

    public AccountDTO createAccount(AccountDTO dto) {
        Account account = new Account();
        account.setUserId(dto.getUserId());
        account.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        account.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "EUR");

        // Generate a random mock IBAN for testing
        String bankCode = "NEXS";
        String countryCode = "IT";
        String checkDigits = String.format("%02d", random.nextInt(100));
        StringBuilder bban = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            bban.append(random.nextInt(10));
        }
        account.setIban(countryCode + checkDigits + bankCode + bban.toString().substring(0, 18)); // Total 27 chars

        account = accountRepository.save(account);
        return toDTO(account);
    }

    public List<AccountDTO> getAccountsByUserId(Long userId) {
        List<Account> accounts = accountRepository.findAllByUserId(userId);
        return accounts.stream().map(AccountService::toDTO).toList();
    }

    public Account getAccountEntityById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }

    public Account getAccountEntityByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new ResourceNotFoundException("Account with IBAN " + iban + " not found"));
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Transactional
    public AccountDTO deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Deposit amount must be positive");
        }

        Account account = getAccountEntityById(accountId);
        account.setBalance(account.getBalance().add(amount));
        account = accountRepository.save(account);

        // Record the deposit as a transaction
        try {
            TransactionDTO depositTxn = new TransactionDTO();
            depositTxn.setUserId(account.getUserId());
            depositTxn.setAccountId(account.getId());
            depositTxn.setType(TransactionType.CREDIT);
            depositTxn.setCategory(TransactionCategory.DEPOSIT);
            depositTxn.setAmount(amount);
            depositTxn.setDescription("Deposit to account");
            depositTxn.setReferenceId(UUID.randomUUID().toString());
            transactionClient.createTransaction(depositTxn);
        } catch (Exception e) {
            System.err.println("Warning: Failed to record deposit transaction: " + e.getMessage());
        }

        return toDTO(account);
    }

    @Transactional
    public AccountDTO updateAccountName(Long accountId, String name) {
        Account account = getAccountEntityById(accountId);
        account.setName(name);
        account = accountRepository.save(account);
        return toDTO(account);
    }

    public static AccountDTO toDTO(Account account) {
        return new AccountDTO(
                account.getId(), account.getUserId(), account.getIban(), account.getBalance(),
                account.getCurrency(), account.getName(), account.getCreatedAt());
    }
}
