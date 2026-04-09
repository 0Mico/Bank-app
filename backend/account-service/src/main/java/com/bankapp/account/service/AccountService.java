package com.bankapp.account.service;

import com.common.dto.AccountDTO;
import com.common.dto.RecipientInfoDTO;
import com.common.model.TransactionModel;
import com.common.enums.TransactionCategory;
import com.common.enums.TransactionType;
import com.common.exception.BadRequestException;
import com.common.exception.ResourceNotFoundException;
import com.bankapp.account.client.TransactionServiceClient;
import com.bankapp.account.entity.Account;
import com.bankapp.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.Random;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionServiceClient transactionClient;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Random random = new Random();

    public AccountService(AccountRepository accountRepository, TransactionServiceClient transactionClient) {
        this.accountRepository = accountRepository;
        this.transactionClient = transactionClient;
    }

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

    public AccountDTO createAccount(AccountDTO dto) {
        Account account = new Account();
        account.setUserId(dto.getUserId());
        account.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        account.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "EUR");        
        account.setIban(createMockIban()); 
        account = accountRepository.save(account);
        return toDTO(account);
    }

    public List<AccountDTO> getAccountsByUserId(Long userId) {
        List<Account> accounts = accountRepository.findAllByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Didn't find any account associated to the user"));
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
            TransactionModel depositTxn = new TransactionModel();
            depositTxn.setUserId(account.getUserId());
            depositTxn.setAccountId(account.getId());
            depositTxn.setType(TransactionType.CREDIT);
            depositTxn.setCategory(TransactionCategory.DEPOSIT);
            depositTxn.setAmount(amount);
            depositTxn.setDescription("Deposit to account");
            depositTxn.setReferenceId(UUID.randomUUID().toString());
            transactionClient.createTransaction(depositTxn);
        } catch (Exception e) {
            logger.warning("Failed to register deposit to " + getAccountEntityById(accountId).getIban());
        }
        return toDTO(account);
    }

    @Transactional
    public void updateBalanceInternal(Long accountId, BigDecimal amountToAdd) {
        Account account = getAccountEntityById(accountId);
        account.setBalance(account.getBalance().add(amountToAdd));
        accountRepository.save(account);
    }

    @Transactional
    public AccountDTO updateAccountName(Long accountId, String name) {
        Account account = getAccountEntityById(accountId);
        account.setName(name);
        account = accountRepository.save(account);
        return toDTO(account);
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

    public static AccountDTO toDTO(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getUserId(),
                account.getIban(),
                account.getBalance(),
                account.getCurrency(),
                account.getName(),
                account.getCreatedAt());
    }
}
