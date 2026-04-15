package com.common.interfaces;

import com.common.dto.TransactionDTO;
import com.common.model.TransactionModel;

public interface TransactionServiceApi {
    TransactionModel createTransaction(TransactionDTO transaction);
}
