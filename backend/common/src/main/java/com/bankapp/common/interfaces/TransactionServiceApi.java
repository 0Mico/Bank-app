package com.bankapp.common.interfaces;

import com.bankapp.common.dto.TransactionDTO;

public interface TransactionServiceApi {
    TransactionDTO createTransaction(TransactionDTO transaction);
    TransactionDTO getTransactionById(Long id);
    TransactionDTO updateTransaction(Long id, TransactionDTO transaction);
    void deleteTransaction(Long id);
}
