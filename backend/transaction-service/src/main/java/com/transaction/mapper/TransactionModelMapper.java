package com.transaction.mapper;

import com.common.dto.TransactionDTO;
import com.common.model.TransactionModel;
import com.transaction.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionModelMapper {

    TransactionModel toModel(Transaction transaction);

    // Used in factory to create new transactions
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category", defaultValue = "OTHER")
    @Mapping(target = "createdAt", ignore = true)
    Transaction dtoToEntity(TransactionDTO dto);
}
