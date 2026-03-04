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
        return ResponseEntity.ok(cardService.getCardsByAccountId(accountId));
    }

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<CardDTO> associateCard(@PathVariable Long accountId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.associateCard(accountId));
    }

    @PatchMapping("/{accountId}/cards/{cardId}/block")
    public ResponseEntity<CardDTO> toggleBlockState(@PathVariable Long accountId, @PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.toggleBlockState(cardId));
    }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
