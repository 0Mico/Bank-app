package com.account.controller;

import com.account.assembler.CardModelAssembler;
import com.account.entity.Card;
import com.account.model.CardModel;
import com.account.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/cards")
public class CardController {

    private final CardService cardService;
    private final CardModelAssembler cardModelAssembler;

    public CardController(CardService cardService, CardModelAssembler cardModelAssembler) {
        this.cardService = cardService;
        this.cardModelAssembler = cardModelAssembler;
    }

    @GetMapping("/{accountId}/cards")
    public ResponseEntity<List<CardModel>> getCardsByAccountId(@PathVariable Long accountId) {
        List<Card> cards = cardService.getCardsByAccountId(accountId);
        return ResponseEntity.ok(cardModelAssembler.toModels(cards));
    }

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<CardModel> associateCard(@PathVariable Long accountId) {
        Card newCard = cardService.associateCard(accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardModelAssembler.toModel(newCard));
    }

    @PatchMapping("/{accountId}/cards/{cardId}/block")
    public ResponseEntity<CardModel> toggleBlockState(@PathVariable Long accountId, @PathVariable Long cardId) {
        Card blockedCard = cardService.toggleBlockState(accountId, cardId);
        return ResponseEntity.ok(cardModelAssembler.toModel(blockedCard));
    }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        cardService.deleteCard(accountId, cardId);
        return ResponseEntity.noContent().build();
    }
}
