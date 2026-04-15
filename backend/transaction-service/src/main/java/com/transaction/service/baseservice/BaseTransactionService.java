package com.transaction.service.baseservice;

import java.util.List;
import com.common.dto.TransactionDTO;
import com.common.interfaces.BaseService;
import com.transaction.dto.TransactionFilter;
import com.transaction.entity.Transaction;
import com.transaction.repository.TransactionRepository;

public interface BaseTransactionService extends BaseService<TransactionRepository, Transaction, Long> {

    Transaction createTransaction(TransactionDTO dto);
    List<Transaction> getTransactions(TransactionFilter filter);
}
