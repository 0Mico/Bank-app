package com.common.interfaces;

import com.common.model.TransactionModel;

public interface TransactionServiceApi {
    TransactionModel createTransaction(TransactionModel transaction);
    /*TransactionModel getTransactionById(Long id);
    TransactionModel updateTransaction(Long id, TransactionModel transaction);
    void deleteTransaction(Long id);
    */
}
