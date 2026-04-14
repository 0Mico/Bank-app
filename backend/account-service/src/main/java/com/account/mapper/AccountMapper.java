package com.account.mapper;

import org.mapstruct.Mapper;
import com.account.entity.Account;
import com.common.model.AccountModel;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountModel toModel(Account account);
}
