package com.bankapp.common.dto;

import com.bankapp.common.enums.TransactionCategory;
import com.bankapp.common.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {
    private Long id;
    private Long userId;
    private Long accountId;
    private TransactionType type;
    private TransactionCategory category;
    private BigDecimal amount;
    private String description;
    private String referenceId;
    private String counterpartyIban;
    private LocalDateTime createdAt;

    public TransactionDTO() {
    }

    public TransactionDTO(Long id, Long userId, Long accountId, TransactionType type, TransactionCategory category,
            BigDecimal amount, String description, String referenceId, String counterpartyIban,
            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.referenceId = referenceId;
        this.counterpartyIban = counterpartyIban;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getCounterpartyIban() {
        return counterpartyIban;
    }

    public void setCounterpartyIban(String counterpartyIban) {
        this.counterpartyIban = counterpartyIban;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
