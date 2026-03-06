package com.bankapp.payment.controller;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.dto.DepositDTO;
import com.bankapp.payment.entity.Account;
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

    @GetMapping("/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccounts(@PathVariable Long userId) {
        List<AccountDTO> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/by-iban")
    public ResponseEntity<AccountDTO> getByIban(@RequestParam String iban) {
        Account account = accountService.getAccountEntityByIban(iban);
        return ResponseEntity.ok(AccountService.toDTO(account));
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long accountId) {
        Account account = accountService.getAccountEntityById(accountId);
        return ResponseEntity.ok(AccountService.toDTO(account));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO dto) {
        AccountDTO account = accountService.createAccount(dto);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountDTO> deposit(@PathVariable Long accountId, @RequestBody DepositDTO dto) {
        AccountDTO account = accountService.deposit(accountId, dto.getAmount());
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{accountId}/name")
    public ResponseEntity<AccountDTO> updateName(@PathVariable Long accountId, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        AccountDTO account = accountService.updateAccountName(accountId, name);
        return ResponseEntity.ok(account);
    }
}