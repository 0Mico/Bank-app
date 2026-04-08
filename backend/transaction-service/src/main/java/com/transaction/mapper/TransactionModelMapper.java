package com.transaction.mapper;

import com.common.model.TransactionModel;
import com.transaction.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionModelMapper {

    TransactionModel toModel(Transaction transaction);
}
