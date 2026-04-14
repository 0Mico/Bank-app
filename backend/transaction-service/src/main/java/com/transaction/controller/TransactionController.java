package com.transaction.controller;

import com.common.dto.TransactionDTO;
import com.common.model.TransactionModel;
import com.common.enums.TransactionCategory;
import com.transaction.assembler.TransactionModelAssembler;
import com.transaction.dto.TransactionFilter;
import com.transaction.entity.Transaction;
import com.transaction.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TransactionModel> createTransaction(@RequestBody TransactionDTO dto) {
        Transaction transaction = transactionService.createTransaction(dto);
        return ResponseEntity.ok(transactionModelAssembler.toModel(transaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionModel>> getTransactions(TransactionFilter filter) {
        List<Transaction> transactions = transactionService.getTransactions(filter);
        return ResponseEntity.ok(transactionModelAssembler.toModels(transactions));
    }

    @GetMapping("/categories")
    public ResponseEntity<TransactionCategory[]> getCategories() {
        return ResponseEntity.ok(TransactionCategory.values());
    }
}
