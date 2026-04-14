package com.account.factory;

import com.account.entity.Account;
import com.account.mapper.AccountMapper;
import com.common.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcreteAccountFactory implements AccountFactory {

    private final AccountMapper accountMapper;

    @Override
    public Account create(AccountDTO dto) {
        return accountMapper.dtoToEntity(dto);
    }
}
