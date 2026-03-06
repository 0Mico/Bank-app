package com.bankapp.payment.controller;

import com.bankapp.common.dto.CardDTO;
import com.bankapp.payment.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments/accounts")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/{accountId}/cards")
    public ResponseEntity<List<CardDTO>> getCardsByAccountId(@PathVariable Long accountId) {
        List<CardDTO> cards = cardService.getCardsByAccountId(accountId);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<CardDTO> associateCard(@PathVariable Long accountId) {
        CardDTO newCard = cardService.associateCard(accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCard);
    }

    @PatchMapping("/{accountId}/cards/{cardId}/block")
    public ResponseEntity<CardDTO> toggleBlockState(@PathVariable Long accountId, @PathVariable Long cardId) {
        CardDTO blockedCard = cardService.toggleBlockState(cardId);
        return ResponseEntity.ok(blockedCard);
    }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
