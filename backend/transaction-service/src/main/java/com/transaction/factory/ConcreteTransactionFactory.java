package com.transaction.factory;

import com.transaction.entity.Transaction;
import com.transaction.mapper.TransactionModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.common.dto.TransactionDTO;

@Component
@RequiredArgsConstructor
public class ConcreteTransactionFactory implements TransactionFactory {

    private final TransactionModelMapper transactionModelMapper;

    @Override
    public Transaction create(TransactionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }
        return transactionModelMapper.dtoToEntity(dto);
    }
}
