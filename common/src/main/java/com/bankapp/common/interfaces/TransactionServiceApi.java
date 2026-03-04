package com.bankapp.common.interfaces;

import com.bankapp.common.dto.TransactionDTO;
import com.bankapp.common.enums.TransactionCategory;
import com.bankapp.common.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionServiceApi {
    TransactionDTO createTransaction(TransactionDTO transaction);

    TransactionDTO getTransactionById(Long id);

    List<TransactionDTO> getTransactions(Long userId, Long accountId, TransactionCategory category,
            TransactionType type, LocalDateTime from, LocalDateTime to);

    TransactionDTO updateTransaction(Long id, TransactionDTO transaction);

    void deleteTransaction(Long id);
}
