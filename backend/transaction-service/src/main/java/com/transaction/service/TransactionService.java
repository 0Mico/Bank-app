package com.transaction.service;

import com.common.dto.TransactionDTO;
import com.transaction.dto.TransactionFilter;
import com.transaction.entity.Transaction;
import com.transaction.factory.TransactionFactory;
import com.transaction.repository.TransactionRepository;
import com.transaction.service.baseservice.BaseTransactionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService implements BaseTransactionService{

    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;

    @Override
    public TransactionRepository getRepository() {
        return this.transactionRepository;
    }

    @Override
    public Transaction createTransaction(TransactionDTO dto) {
        Transaction txn = transactionFactory.create(dto);
        txn = transactionRepository.save(txn);
        return txn;
    }

    @Override
    public List<Transaction> getTransactions(TransactionFilter filter) {
        return transactionRepository.findWithFilters(
            filter.getUserId(),
            filter.getAccountId(),
            filter.getCategory() != null ? filter.getCategory().name() : null,
            filter.getType() != null ? filter.getType().name() : null,
            filter.getFrom() != null ? filter.getFrom().atOffset(ZoneOffset.UTC).toString() : null,
            filter.getTo() != null ? filter.getTo().atOffset(ZoneOffset.UTC).toString() : null)
                .stream()
                .toList();
    }
}
