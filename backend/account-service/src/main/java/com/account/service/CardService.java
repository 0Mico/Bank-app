package com.account.service;

import java.util.List;

import com.account.factory.CardFactory;
import com.account.factory.ConcreteCardFactory;
import org.springframework.stereotype.Service;

import com.common.exception.ResourceNotFoundException;
import com.common.exception.UnauthorizedException;
import com.account.entity.Card;
import com.account.repository.CardRepository;
import com.account.repository.AccountRepository;

@Service
public class CardService {

    private final CardRepository cardRepo;
    private final AccountRepository accountRepo;
    private final CardFactory cardFactory;

    public CardService(CardRepository cardRepo, AccountRepository accountRepo, ConcreteCardFactory cardFactory) {
        this.cardRepo = cardRepo;
        this.accountRepo = accountRepo;
        this.cardFactory = cardFactory;
    }

    public List<Card> getCardsByAccountId(Long accountId) {
        return cardRepo.findAllByAccountId(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

    public Card associateCard(Long accountId) {
        if (!accountRepo.existsById(accountId)) {
            throw new ResourceNotFoundException("Account", accountId);
        }
        
        Card card = cardFactory.create(accountId);
        return cardRepo.save(card);
    }

    public Card toggleBlockState(Long accountId, Long cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card", cardId));
        if(!card.getAccountId().equals(accountId)){
            throw new UnauthorizedException("Card does not belong to this account");
        }
        card.setBlocked(!card.isBlocked());
        return cardRepo.save(card);
    }

    public void deleteCard(Long accountId, Long cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card", cardId));
        if(!card.getAccountId().equals(accountId)){
            throw new UnauthorizedException("Card does not belong to this account");
        }
        cardRepo.delete(card);
    }

}
