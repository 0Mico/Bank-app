package com.bankapp.payment.service;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.dto.PaymentDTO;
import com.bankapp.common.dto.TransactionDTO;
import com.bankapp.common.enums.PaymentStatus;
import com.bankapp.common.enums.TransactionCategory;
import com.bankapp.common.enums.TransactionType;
import com.bankapp.common.exception.BadRequestException;
import com.bankapp.common.exception.InsufficientFundsException;
import com.bankapp.common.exception.ResourceNotFoundException;
import com.bankapp.common.interfaces.PaymentServiceApi;
import com.bankapp.payment.client.TransactionServiceClient;
import com.bankapp.payment.entity.Account;
import com.bankapp.payment.entity.Payment;
import com.bankapp.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService implements PaymentServiceApi {

    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final TransactionServiceClient transactionClient;

    public PaymentService(PaymentRepository paymentRepository, AccountService accountService,
            TransactionServiceClient transactionClient) {
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
        this.transactionClient = transactionClient;
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be positive");
        }

        Account fromAccount = accountService.getAccountEntityById(dto.getFromAccountId());

        if (dto.getToIban() == null || dto.getToIban().isBlank()) {
            throw new BadRequestException("Recipient IBAN is required");
        }
        Account toAccount = accountService.getAccountEntityByIban(dto.getToIban());

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new BadRequestException("Cannot send payment to yourself");
        }
        if (fromAccount.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + fromAccount.getBalance());
        }

        debitSender(fromAccount, dto.getAmount());
        creditReceiver(toAccount, dto.getAmount());

        String userDesc = dto.getDescription();
        boolean hasDesc = userDesc != null && !userDesc.trim().isEmpty();

        Payment payment = createPayment(fromAccount, toAccount, dto, hasDesc);

        // Record transactions via transaction-service
        String refId = UUID.randomUUID().toString();
        try {
            TransactionDTO debitTxn = new TransactionDTO();
            debitTxn.setUserId(fromAccount.getUserId());
            debitTxn.setAccountId(fromAccount.getId());
            debitTxn.setType(TransactionType.DEBIT);
            debitTxn.setCategory(payment.getCategory());
            debitTxn.setAmount(dto.getAmount());
            debitTxn.setDescription(hasDesc ? userDesc : "Payment to " + toAccount.getIban());
            debitTxn.setReferenceId(refId);
            debitTxn.setCounterpartyIban(toAccount.getIban());
            transactionClient.createTransaction(debitTxn);

            TransactionDTO creditTxn = new TransactionDTO();
            creditTxn.setUserId(toAccount.getUserId());
            creditTxn.setAccountId(toAccount.getId());
            creditTxn.setType(TransactionType.CREDIT);
            creditTxn.setCategory(payment.getCategory());
            creditTxn.setAmount(dto.getAmount());
            creditTxn.setDescription(hasDesc ? userDesc : "Payment from " + fromAccount.getIban());
            creditTxn.setReferenceId(refId);
            creditTxn.setCounterpartyIban(fromAccount.getIban());
            transactionClient.createTransaction(creditTxn);
        } catch (Exception e) {
            // Log error but don't fail the payment — transactions are supplementary
            System.err.println("Warning: Failed to record transaction: " + e.getMessage());
        }

        return toDTO(payment, fromAccount, toAccount);
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        Account from = accountService.getAccountEntityById(payment.getFromAccountId());
        Account to = accountService.getAccountEntityById(payment.getToAccountId());
        return toDTO(payment, from, to);
    }

    @Override
    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(p -> {
                    Account from = accountService.getAccountEntityById(p.getFromAccountId());
                    Account to = accountService.getAccountEntityById(p.getToAccountId());
                    return toDTO(p, from, to);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountDTO> getAccountsByUserId(Long userId) {
        return accountService.getAccountsByUserId(userId);
    }

    @Override
    public AccountDTO createAccount(AccountDTO account) {
        return accountService.createAccount(account);
    }

    private PaymentDTO toDTO(Payment payment, Account from, Account to) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setFromAccountId(payment.getFromAccountId());
        dto.setToAccountId(payment.getToAccountId());
        dto.setFromUserId(from.getUserId());
        dto.setToUserId(to.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(from.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setCategory(payment.getCategory());
        dto.setDescription(payment.getDescription());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    private void debitSender(Account fromAccount, BigDecimal amount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountService.saveAccount(fromAccount);
    }

    private void creditReceiver(Account toAccount, BigDecimal amount) {
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountService.saveAccount(toAccount);
    }

    private Payment createPayment(Account fromAccount, Account toAccount, PaymentDTO dto, boolean hasDesc) {
        Payment payment = new Payment();
        payment.setFromAccountId(fromAccount.getId());
        payment.setToAccountId(toAccount.getId());
        payment.setAmount(dto.getAmount());
        payment.setDescription(hasDesc ? dto.getDescription() : "Payment to " + fromAccount.getIban());
        payment.setCategory(dto.getCategory() != null ? dto.getCategory() : TransactionCategory.TRANSFER);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);
        return payment;
    }
}
