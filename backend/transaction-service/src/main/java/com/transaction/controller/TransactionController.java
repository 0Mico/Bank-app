package com.transaction.controller;

import com.common.model.TransactionModel;
import com.common.enums.TransactionCategory;
import com.common.enums.TransactionType;
import com.transaction.assembler.TransactionModelAssembler;
import com.transaction.entity.Transaction;
import com.transaction.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionModelAssembler transactionModelAssembler;

    public TransactionController(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler) {
        this.transactionService = transactionService;
        this.transactionModelAssembler = transactionModelAssembler;
    }

    @PostMapping
    public ResponseEntity<TransactionModel> createTransaction(@RequestBody TransactionModel dto) {
        Transaction transaction = transactionService.createTransaction(dto);
        return ResponseEntity.ok(transactionModelAssembler.toModel(transaction));
    }

    /*
    @GetMapping("/{id}")
    public ResponseEntity<TransactionModel> getTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transactionModelAssembler.toModel(transaction));
    }
    */

    @GetMapping
    public ResponseEntity<List<TransactionModel>> getTransactions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) TransactionCategory category,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<Transaction> transactions = transactionService.getTransactions(userId, accountId, category, type, from, to);
        return ResponseEntity.ok(transactionModelAssembler.toModels(transactions));
    }

    /*
    @PutMapping("/{id}")
    public ResponseEntity<TransactionModel> updateTransaction(@PathVariable Long id, @RequestBody TransactionModel dto) {
        Transaction transaction = transactionService.updateTransaction(id, dto);
        return ResponseEntity.ok(transactionModelAssembler.toModel(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
    */

    @GetMapping("/categories")
    public ResponseEntity<TransactionCategory[]> getCategories() {
        return ResponseEntity.ok(TransactionCategory.values());
    }
}
