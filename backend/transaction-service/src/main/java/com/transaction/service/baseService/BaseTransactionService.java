package com.transaction.service.baseService;

import java.time.LocalDateTime;
import java.util.List;

import com.common.dto.TransactionDTO;
import com.common.enums.TransactionCategory;
import com.common.enums.TransactionType;
import com.common.interfaces.BaseService;
import com.transaction.entity.Transaction;
import com.transaction.repository.TransactionRepository;

public interface BaseTransactionService extends BaseService<TransactionRepository, Transaction, Long> {

    Transaction createTransaction(TransactionDTO dto);
    List<Transaction> getTransactions(Long userId, Long accountId, TransactionCategory category,
                                        TransactionType type, LocalDateTime from, LocalDateTime to);
}
