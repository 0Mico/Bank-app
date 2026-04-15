package com.payment.service;

import com.common.model.AccountModel;
import com.common.dto.TransactionDTO;
import com.common.exception.BadRequestException;
import com.common.exception.InsufficientFundsException;
import com.common.exception.PaymentCompensationException;
import com.common.interfaces.AccountServiceApi;
import com.common.interfaces.TransactionServiceApi;
import com.payment.dto.PaymentDTO;
import com.payment.entity.Payment;
import com.payment.factory.PaymentFactory;
import com.payment.mapper.PaymentToTransactionMapper;
import com.payment.repository.PaymentRepository;
import com.payment.service.baseservice.BasePaymentService;
import com.payment.utils.MoneyTransferHelper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService implements BasePaymentService {

    private final Logger logger = Logger.getLogger(PaymentService.class.getName());

    private final PaymentRepository paymentRepository;
    private final AccountServiceApi accountServiceClient;
    private final TransactionServiceApi transactionClient;
    private final PaymentFactory paymentFactory;
    private final PaymentToTransactionMapper transactionMapper;
    private final MoneyTransferHelper moneyTransferHelper;

    @Override
    public PaymentRepository getRepository() {
        return this.paymentRepository;
    }

    @Transactional
    @Override
    public Payment processPayment(PaymentDTO dto) {
        AccountModel fromAccount = accountServiceClient.getAccountById(dto.getFromAccountId());   
        AccountModel toAccount = accountServiceClient.getAccountByIban(dto.getToIban());

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new BadRequestException("Cannot send payment to yourself");
        }
        if (fromAccount.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + fromAccount.getBalance());
        }

        moneyTransferHelper.debitSender(fromAccount.getId(), dto.getAmount());
        try {
            moneyTransferHelper.creditReceiver(toAccount.getId(), dto.getAmount());
        } catch (Exception e) {
            try {
                moneyTransferHelper.creditReceiver(fromAccount.getId(), dto.getAmount()); // Reverse debit
            } catch (Exception compensatiException) {
                throw new PaymentCompensationException("Payment went wrong. Failed debit compensation for account " + fromAccount.getId());
            }
            throw new BadRequestException("Payment went wrong. Failed credit for account " + toAccount.getId());
        }

        dto.setToAccountId(toAccount.getId());
        dto.setFromAccountIban(fromAccount.getIban());

        Payment payment = paymentFactory.create(dto);
        payment = paymentRepository.save(payment);

        String userDesc = dto.getDescription();
        boolean hasDesc = userDesc != null && !userDesc.trim().isEmpty();

        // Record transactions via transaction-service
        String refId = UUID.randomUUID().toString();
        try {
            TransactionDTO debitTxn = transactionMapper.toDebitDto(payment, fromAccount, toAccount, refId, hasDesc);
            transactionClient.createTransaction(debitTxn);

            TransactionDTO creditTxn = transactionMapper.toCreditDto(payment, fromAccount, toAccount, refId, hasDesc);
            transactionClient.createTransaction(creditTxn);
        } catch (Exception e) {
            // Log error but don't fail the payment — transactions are supplementary
            logger.info("Warning: Failed to record transaction: " + e.getMessage());
        }
        return payment;
    }
}