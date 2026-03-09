package com.bankapp.account.controller;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.dto.DepositDTO;
import com.bankapp.account.entity.Account;
import com.bankapp.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long accountId) {
        Account account = accountService.getAccountEntityById(accountId);
        return ResponseEntity.ok(AccountService.toDTO(account));
    }

    @GetMapping("/userId")
    public ResponseEntity<List<AccountDTO>> getAccounts(@RequestParam Long userId) {
        List<AccountDTO> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/iban")
    public ResponseEntity<AccountDTO> getByIban(@RequestParam String iban) {
        Account account = accountService.getAccountEntityByIban(iban);
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

    @PutMapping("/internal/{accountId}/balance")
    public ResponseEntity<Void> updateBalanceInternal(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        accountService.updateBalanceInternal(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }
}