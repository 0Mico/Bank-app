package com.account.factory;

import com.account.entity.Card;
import com.account.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcreteCardFactory implements CardFactory {

    private final CardMapper cardMapper;

    @Override
    public Card create(Long accountId){
        return cardMapper.toEntity(accountId);
    }
}
