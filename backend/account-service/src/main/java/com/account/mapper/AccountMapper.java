package com.account.mapper;

import com.account.entity.Account;
import com.account.util.AccountUtils;
import com.common.dto.AccountDTO;
import com.common.model.AccountModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    @Autowired
    protected AccountUtils accountUtils;

    public abstract AccountModel toModel(Account account);

    @Mapping(target = "iban", expression = "java(accountUtils.createMockIban())")
    @Mapping(target = "balance", source = "balance", defaultValue = "0")
    @Mapping(target = "currency", source = "currency", defaultValue = "EUR")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Account dtoToEntity(AccountDTO dto);
}
