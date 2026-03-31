package com.common.interfaces;

import com.common.dto.TransactionDTO;

public interface TransactionServiceApi {
    TransactionDTO createTransaction(TransactionDTO transaction);
    TransactionDTO getTransactionById(Long id);
    TransactionDTO updateTransaction(Long id, TransactionDTO transaction);
    void deleteTransaction(Long id);
}
