
package com.account.controller;

import com.account.assembler.AccountModelAssembler;
import com.account.dtos.DepositDTO;
import com.account.entity.Account;
import com.account.service.AccountService;
import com.common.dto.AccountDTO;
import com.common.model.AccountModel;
import com.common.model.RecipientInfoModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountModelAssembler accountModelAssembler;

    public AccountController(AccountService accountService, AccountModelAssembler accountModelAssembler) {
        this.accountService = accountService;
        this.accountModelAssembler = accountModelAssembler;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountModel> getAccountById(@PathVariable Long accountId) {
        Account account = accountService.getAccountEntityById(accountId);
        return ResponseEntity.ok(accountModelAssembler.toModel(account));
    }

    @GetMapping("/userId")
    public ResponseEntity<List<AccountModel>> getAccounts(@RequestParam Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accountModelAssembler.toModels(accounts));
    }

    @GetMapping("/iban")
    public ResponseEntity<AccountModel> getByIban(@RequestParam String iban) {
        Account account = accountService.getAccountEntityByIban(iban);
        return ResponseEntity.ok(accountModelAssembler.toModel(account));
    }

    @PostMapping
    public ResponseEntity<AccountModel> createAccount(@RequestBody AccountDTO dto) {
        Account account = accountService.createAccount(dto);
        return ResponseEntity.ok(accountModelAssembler.toModel(account));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountModel> deposit(@PathVariable Long accountId, @RequestBody DepositDTO dto) {
        Account account = accountService.deposit(accountId, dto.getAmount());
        return ResponseEntity.ok(accountModelAssembler.toModel(account));
    }

    @PatchMapping("/{accountId}/name")
    public ResponseEntity<AccountModel> updateName(@PathVariable Long accountId, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        Account account = accountService.updateAccountName(accountId, name);
        return ResponseEntity.ok(accountModelAssembler.toModel(account));
    }

    @PutMapping("/internal/{accountId}/balance")
    public ResponseEntity<Void> updateBalanceInternal(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        accountService.updateBalanceInternal(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ownership-status")
    public ResponseEntity<RecipientInfoModel> getOwnershipStatus(@RequestParam Long senderAccountId, @RequestParam String recipientIban) {
        RecipientInfoModel info = accountService.determineOwnershipStatus(senderAccountId, recipientIban);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }
}