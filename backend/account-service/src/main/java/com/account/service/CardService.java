package com.account.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.common.exception.ResourceNotFoundException;
import com.common.exception.UnauthorizedException;
import com.account.entity.Card;
import com.account.dtos.CardDTO;
import com.account.entity.Account;
import com.account.repository.CardRepository;
import com.account.repository.AccountRepository;

@Service
public class CardService {

    private final CardRepository cardRepo;
    private final AccountRepository accountRepo;
    private final Random random = new Random();


    public CardService(CardRepository cardRepo, AccountRepository accountRepo) {
        this.cardRepo = cardRepo;
        this.accountRepo = accountRepo;
    }

    private String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int block = random.nextInt(10000);
            cardNumber.append(String.format("%04d", block));
            if (i < 3) {
                cardNumber.append("-");
            }
        }
        return cardNumber.toString();
    }

    private Card createNewCard(Account account) {
        Card card = new Card();
        card.setAccount(account);
        card.setCardNumber(generateCardNumber());
        
        card.setExpiration(LocalDate.now().plusYears(5));
        return card;
    }

    public List<CardDTO> getCardsByAccountId(Long accountId) {
        List<Card> cards = cardRepo.findAllByAccount_Id(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        return cards.stream().map(this::mapToDTO).toList();
    }

    public CardDTO associateCard(Long accountId) {
        Account account = accountRepo.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        
        Card card = createNewCard(account);
        Card savedCard = cardRepo.save(card);
        return mapToDTO(savedCard);
    }

    public CardDTO toggleBlockState(Long accountId, Long cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card", cardId));
        if(!card.getAccount().getId().equals(accountId)){
            throw new UnauthorizedException("Card does not belong to this account");
        }
        card.setBlocked(!card.isBlocked());
        Card savedCard = cardRepo.save(card);
        return mapToDTO(savedCard);
    }

    public void deleteCard(Long accountId, Long cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card", cardId));
        if(!card.getAccount().getId().equals(accountId)){
            throw new UnauthorizedException("Card does not belong to this account");
        }
        cardRepo.delete(card);
    }

    private CardDTO mapToDTO(Card card) {
        return new CardDTO(card.getId(), card.getAccount().getId(), card.getCardNumber(), card.getExpiration(), card.isBlocked());
    }
}
