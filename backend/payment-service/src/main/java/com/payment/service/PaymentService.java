package com.payment.service;

import com.payment.client.TransactionServiceClient;
import com.common.dto.AccountDTO;
import com.common.model.TransactionModel;
import com.common.enums.PaymentStatus;
import com.common.enums.TransactionCategory;
import com.common.enums.TransactionType;
import com.common.exception.BadRequestException;
import com.common.exception.InsufficientFundsException;
import com.common.exception.PaymentCompensationException;
import com.common.exception.ResourceNotFoundException;
import com.common.interfaces.AccountServiceApi;
import com.common.interfaces.TransactionServiceApi;
import com.payment.dtos.PaymentRequest;
import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.logging.Logger;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final Logger logger = Logger.getLogger(PaymentService.class.getName());

    private final PaymentRepository paymentRepository;
    private final AccountServiceApi accountServiceClient;
    private final TransactionServiceApi transactionClient;

    public PaymentService(PaymentRepository paymentRepository, AccountServiceApi accountServiceClient,
            TransactionServiceClient transactionClient) {
        this.paymentRepository = paymentRepository;
        this.accountServiceClient = accountServiceClient;
        this.transactionClient = transactionClient;
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        AccountDTO fromAccount = accountServiceClient.getAccountById(request.getFromAccountId());   
        AccountDTO toAccount = accountServiceClient.getAccountByIban(request.getToIban());

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new BadRequestException("Cannot send payment to yourself");
        }
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + fromAccount.getBalance());
        }

        debitSender(fromAccount.getId(), request.getAmount());
        try {
            creditReceiver(toAccount.getId(), request.getAmount());
        } catch (Exception e) {
            try {
                creditReceiver(fromAccount.getId(), request.getAmount()); // Reverse debit
            } catch (Exception compensatiException) {
                throw new PaymentCompensationException("Payment went wrong. Failed debit compensation for account " + fromAccount.getId());
            }
            throw new BadRequestException("Payment went wrong. Failed credit for account " + toAccount.getId());
        }

        String userDesc = request.getDescription();
        boolean hasDesc = userDesc != null && !userDesc.trim().isEmpty();

        Payment payment = createPayment(fromAccount, toAccount, request, hasDesc);

        // Record transactions via transaction-service
        String refId = UUID.randomUUID().toString();
        try {
            TransactionModel debitTxn = new TransactionModel();
            debitTxn.setUserId(fromAccount.getUserId());
            debitTxn.setAccountId(fromAccount.getId());
            debitTxn.setType(TransactionType.DEBIT);
            debitTxn.setCategory(payment.getCategory());
            debitTxn.setAmount(request.getAmount());
            debitTxn.setDescription(hasDesc ? userDesc : "Payment to " + toAccount.getIban());
            debitTxn.setReferenceId(refId);
            debitTxn.setCounterpartyIban(toAccount.getIban());
            transactionClient.createTransaction(debitTxn);

            TransactionModel creditTxn = new TransactionModel();
            creditTxn.setUserId(toAccount.getUserId());
            creditTxn.setAccountId(toAccount.getId());
            creditTxn.setType(TransactionType.CREDIT);
            creditTxn.setCategory(payment.getCategory());
            creditTxn.setAmount(request.getAmount());
            creditTxn.setDescription(hasDesc ? userDesc : "Payment from " + fromAccount.getIban());
            creditTxn.setReferenceId(refId);
            creditTxn.setCounterpartyIban(fromAccount.getIban());
            transactionClient.createTransaction(creditTxn);
        } catch (Exception e) {
            // Log error but don't fail the payment — transactions are supplementary
            logger.info("Warning: Failed to record transaction: " + e.getMessage());
        }
        return payment;
    }

    /*
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }

    public List<Payment> getPaymentsByUserId(Long userId) {
        List<Long> accountIds = accountServiceClient.getAccountsByUserId(userId).stream()
                .map(AccountDTO::getId).toList();
        if (accountIds.isEmpty()) {
            return List.of();
        }
        return paymentRepository.findByFromAccountIdInOrToAccountIdInOrderByCreatedAtDesc(accountIds, accountIds);
    }
    */


    private void debitSender(Long fromAccountId, BigDecimal amount) {
        accountServiceClient.updateBalanceInternal(fromAccountId, amount.negate());
    }

    private void creditReceiver(Long toAccountId, BigDecimal amount) {
         accountServiceClient.updateBalanceInternal(toAccountId, amount);
    }

    private Payment createPayment(AccountDTO fromAccount, AccountDTO toAccount, PaymentRequest request, boolean hasDesc) {
        Payment payment = new Payment();
        payment.setFromAccountId(fromAccount.getId());
        payment.setToAccountId(toAccount.getId());
        payment.setAmount(request.getAmount());
        payment.setDescription(hasDesc ? request.getDescription() : "Payment to " + fromAccount.getIban());
        payment.setCategory(request.getCategory() != null ? request.getCategory() : TransactionCategory.TRANSFER);
        payment.setStatus(PaymentStatus.COMPLETED);
        return paymentRepository.save(payment);
    }
}
