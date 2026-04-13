package com.account.mapper;

import com.account.entity.Card;
import com.account.model.CardModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "accountId", source = "account.id")
    CardModel toModel(Card card);
}
