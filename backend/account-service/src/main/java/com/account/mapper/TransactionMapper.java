package com.account.mapper;

import com.account.entity.Account;
import com.common.dto.TransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = { UUID.class })
public interface TransactionMapper {

    @Mapping(target = "userId", source = "account.userId")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "type", constant = "CREDIT")
    @Mapping(target = "category", constant = "DEPOSIT")
    @Mapping(target = "description", constant = "Deposit to account")
    @Mapping(target = "referenceId", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "counterpartyIban", ignore = true)
    TransactionDTO toDepositTransactionDto(Account account, BigDecimal amount);
}
