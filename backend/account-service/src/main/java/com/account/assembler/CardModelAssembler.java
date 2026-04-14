package com.account.assembler;

import com.account.entity.Card;
import com.account.mapper.CardMapper;
import com.account.model.CardModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CardModelAssembler {
    private final CardMapper cardMapper;

    public CardModelAssembler(CardMapper cardMapper) {
        this.cardMapper = cardMapper;
    }

    public CardModel toModel(Card card) {
        return cardMapper.toModel(card);
    }

    public List<CardModel> toModels(List<Card> cards) {
        return cards.stream()
                .map(this::toModel)
                .toList();
    }
}
