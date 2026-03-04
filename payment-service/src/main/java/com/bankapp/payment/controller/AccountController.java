package com.bankapp.payment.controller;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.dto.DepositDTO;
import com.bankapp.payment.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.createAccount(dto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccounts(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountDTO> deposit(@PathVariable Long accountId, @RequestBody DepositDTO dto) {
        return ResponseEntity.ok(accountService.deposit(accountId, dto.getAmount()));
    }

    @PatchMapping("/{accountId}/name")
    public ResponseEntity<AccountDTO> updateName(@PathVariable Long accountId, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        return ResponseEntity.ok(accountService.updateAccountName(accountId, name));
    }

    @GetMapping("/by-iban")
    public ResponseEntity<AccountDTO> getByIban(@RequestParam String iban) {
        return ResponseEntity.ok(AccountService.toDTO(accountService.getAccountEntityByIban(iban)));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long accountId) {
        return ResponseEntity.ok(AccountService.toDTO(accountService.getAccountEntityById(accountId)));
    }
}
