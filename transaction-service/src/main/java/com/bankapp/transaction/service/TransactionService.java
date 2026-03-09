package com.bankapp.transaction.service;

import com.bankapp.common.dto.TransactionDTO;
import com.bankapp.common.enums.TransactionCategory;
import com.bankapp.common.enums.TransactionType;
import com.bankapp.common.exception.ResourceNotFoundException;
import com.bankapp.common.interfaces.TransactionServiceApi;
import com.bankapp.transaction.entity.Transaction;
import com.bankapp.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService implements TransactionServiceApi {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionDTO createTransaction(TransactionDTO dto) {
        Transaction txn = new Transaction();
        txn.setUserId(dto.getUserId());
        txn.setAccountId(dto.getAccountId());
        txn.setType(dto.getType());
        txn.setCategory(dto.getCategory() != null ? dto.getCategory() : TransactionCategory.OTHER);
        txn.setAmount(dto.getAmount());
        txn.setDescription(dto.getDescription());
        txn.setReferenceId(dto.getReferenceId());
        txn.setCounterpartyIban(dto.getCounterpartyIban());
        txn = transactionRepository.save(txn);
        return toDTO(txn);
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
        return toDTO(txn);
    }

    @Override
    public List<TransactionDTO> getTransactions(Long userId, Long accountId, TransactionCategory category,
            TransactionType type, LocalDateTime from, LocalDateTime to) {
        return transactionRepository.findWithFilters(userId, accountId, category, type, from, to)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO updateTransaction(Long id, TransactionDTO dto) {
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        if (dto.getCategory() != null)
            txn.setCategory(dto.getCategory());
        if (dto.getDescription() != null)
            txn.setDescription(dto.getDescription());
        if (dto.getType() != null)
            txn.setType(dto.getType());

        txn = transactionRepository.save(txn);
        return toDTO(txn);
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction", id);
        }
        transactionRepository.deleteById(id);
    }

    private TransactionDTO toDTO(Transaction txn) {
        return new TransactionDTO(
                txn.getId(), txn.getUserId(), txn.getAccountId(), txn.getType(), txn.getCategory(),
                txn.getAmount(), txn.getDescription(), txn.getReferenceId(), txn.getCounterpartyIban(),
                txn.getCreatedAt());
    }
}
