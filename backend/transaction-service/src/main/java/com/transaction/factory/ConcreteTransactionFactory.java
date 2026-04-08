package com.transaction.factory;

import com.transaction.entity.Transaction;

import org.springframework.stereotype.Component;

import com.common.enums.TransactionCategory;
import com.common.model.TransactionModel;

@Component
public class ConcreteTransactionFactory implements TransactionFactory {

    @Override
    public Transaction create(TransactionModel dto) {
        Transaction txn = new Transaction();
        txn.setUserId(dto.getUserId());
        txn.setAccountId(dto.getAccountId());
        txn.setType(dto.getType());
        txn.setCategory(dto.getCategory() != null ? dto.getCategory() : TransactionCategory.OTHER);
        txn.setAmount(dto.getAmount());
        txn.setDescription(dto.getDescription());
        txn.setReferenceId(dto.getReferenceId());
        txn.setCounterpartyIban(dto.getCounterpartyIban());
        return txn;
    }
}
