package com.account.assembler;

import com.account.entity.Account;
import com.account.mapper.AccountMapper;
import com.common.model.AccountModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountModelAssembler {

    private final AccountMapper accountMapper;

    public AccountModelAssembler(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public AccountModel toModel(Account account) {
        return accountMapper.toModel(account);
    }

    public List<AccountModel> toModels(List<Account> accounts) {
        return accounts.stream()
                .map(this::toModel)
                .toList();
    }
}
