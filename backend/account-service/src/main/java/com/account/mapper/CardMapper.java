package com.account.mapper;

import com.account.entity.Card;
import com.account.model.CardModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardModel toModel(Card card);
}
