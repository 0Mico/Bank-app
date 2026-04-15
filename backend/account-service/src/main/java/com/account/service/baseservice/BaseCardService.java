package com.account.service.baseservice;

import com.account.entity.Card;
import com.account.repository.CardRepository;
import com.common.interfaces.BaseService;

import java.util.List;

public interface BaseCardService extends BaseService<CardRepository, Card, Long> {

    List<Card> getCardsByAccountId(Long accountId);
    Card associateCard(Long accountId);
    Card toggleBlockState(Long accountId, Long cardId);
    void deleteCard(Long accountId, Long cardId);
}
