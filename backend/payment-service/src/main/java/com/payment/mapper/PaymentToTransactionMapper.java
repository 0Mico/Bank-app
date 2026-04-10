package com.payment.mapper;

import com.common.dto.AccountDTO;
import com.common.dto.TransactionDTO;
import com.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentToTransactionMapper {

    @Mapping(target = "userId", source = "fromAccount.userId")
    @Mapping(target = "accountId", source = "fromAccount.id")
    @Mapping(target = "type", constant = "DEBIT")
    @Mapping(target = "description", expression = "java(hasDesc ? payment.getDescription() : \"Payment to \" + toAccount.getIban())")
    @Mapping(target = "counterpartyIban", source = "toAccount.iban")
    TransactionDTO toDebitDto(Payment payment, AccountDTO fromAccount, AccountDTO toAccount, String referenceId, boolean hasDesc);

    @Mapping(target = "userId", source = "toAccount.userId")
    @Mapping(target = "accountId", source = "toAccount.id")
    @Mapping(target = "type", constant = "CREDIT")
    @Mapping(target = "description", expression = "java(hasDesc ? payment.getDescription() : \"Payment from \" + fromAccount.getIban())")
    @Mapping(target = "counterpartyIban", source = "fromAccount.iban")
    TransactionDTO toCreditDto(Payment payment, AccountDTO fromAccount, AccountDTO toAccount, String referenceId, boolean hasDesc);
}
