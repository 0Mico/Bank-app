package com.transaction.service;

import com.common.model.TransactionModel;
import com.common.enums.TransactionCategory;
import com.common.enums.TransactionType;
//import com.common.exception.ResourceNotFoundException;
import com.transaction.entity.Transaction;
import com.transaction.factory.TransactionFactory;
import com.transaction.repository.TransactionRepository;
import com.transaction.service.baseService.BaseTransactionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Transaction createTransaction(TransactionModel dto) {
        Transaction txn = transactionFactory.create(dto);
        txn = transactionRepository.save(txn);
        return txn;
    }

    /*
    Inutilizzato

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }
    */

    @Override
    public List<Transaction> getTransactions(Long userId, Long accountId, TransactionCategory category,
                                                  TransactionType type, LocalDateTime from, LocalDateTime to) {
        return transactionRepository.findWithFilters(
            userId,
            accountId,
            category != null ? category.name() : null,
            type != null ? type.name() : null,
            from != null ? from.atOffset(ZoneOffset.UTC).toString() : null,
            to != null ? to.atOffset(ZoneOffset.UTC).toString() : null)
                .stream()
                .toList();
    }

    /*
    Inutilizzato

    public Transaction updateTransaction(Long id, TransactionModel dto) {
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        if (dto.getCategory() != null)
            txn.setCategory(dto.getCategory());
        if (dto.getDescription() != null)
            txn.setDescription(dto.getDescription());
        if (dto.getType() != null)
            txn.setType(dto.getType());

        txn = transactionRepository.save(txn);
        return txn;
    }

    Iutilizzato 
    
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction", id);
        }
        transactionRepository.deleteById(id);
    }
    */
}
