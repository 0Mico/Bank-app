package com.transaction.assembler;

import com.common.model.TransactionModel;
import com.transaction.entity.Transaction;
import com.transaction.mapper.TransactionModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionModelAssembler {

    private final TransactionModelMapper transactionMapper;

    public TransactionModelAssembler(TransactionModelMapper mapper) {
        this.transactionMapper = mapper;
    }

    public TransactionModel toModel(Transaction transaction) {
        return transactionMapper.toModel(transaction);
    }

    public List<TransactionModel> toModels(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toModel)
                .toList();
    }
}
