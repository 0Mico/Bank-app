package com.bankapp.payment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Calendar;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.bankapp.common.exception.ResourceNotFoundException;
import com.bankapp.common.dto.CardDTO;
import com.bankapp.payment.entity.Card;
import com.bankapp.payment.entity.Account;
import com.bankapp.payment.repository.CardRepository;
import com.bankapp.payment.repository.AccountRepository;

@Service
public class CardService {

    private final CardRepository cardRepo;
    private final AccountRepository accountRepo;

    public CardService(CardRepository cardRepo, AccountRepository accountRepo) {
        this.cardRepo = cardRepo;
        this.accountRepo = accountRepo;
    }

    private String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        Random random = new Random();
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
        card.setAccountId(account.getId());
        card.setCardNumber(generateCardNumber());
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        card.setExpiration(cal.getTime());
        return card;
    }

    public List<CardDTO> getCardsByAccountId(Long accountId) {
        ArrayList<Card> cards = cardRepo.findAllByAccountId(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        return cards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public CardDTO associateCard(Long accountId) {
        Account account = accountRepo.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        
        Card card = createNewCard(account);
        Card savedCard = cardRepo.save(card);
        return mapToDTO(savedCard);
    }

    public CardDTO toggleBlockState(Long cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card", cardId));
        card.setBlocked(!card.isBlocked());
        Card savedCard = cardRepo.save(card);
        return mapToDTO(savedCard);
    }

    public void deleteCard(Long cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card", cardId));
        cardRepo.delete(card);
    }

    private CardDTO mapToDTO(Card card) {
        return new CardDTO(card.getId(), card.getAccountId(), card.getCardNumber(), card.getExpiration(), card.isBlocked());
    }
}
