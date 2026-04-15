package com.account.mapper;

import com.account.entity.Card;
import com.account.model.CardModel;
import com.account.util.AccountUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CardMapper {

    protected AccountUtils accountUtils;
    
    @Autowired
    public void setAccountUtils(AccountUtils accountUtils) {
        this.accountUtils = accountUtils;
    }

    public abstract CardModel toModel(Card card);

    @Mapping(target = "cardNumber", expression = "java(accountUtils.generateCardNumber())")
    @Mapping(target = "expiration", expression = "java(java.time.LocalDate.now().plusYears(5))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blocked", constant = "false")
    public abstract Card toEntity(Long accountId);
}
