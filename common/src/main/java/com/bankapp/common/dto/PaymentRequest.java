package com.bankapp.common.dto;

import java.math.BigDecimal;

import com.bankapp.common.enums.TransactionCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {

    @NotBlank
    private Long fromAccountId;

    @NotBlank(message = "Recipient iban is required")
    private String toIban;

    @NotNull(message = "Amount is required")
    @Positive(message = "ammount must be > 0")
    private BigDecimal amount;

    private TransactionCategory category;
    private String description;

    public PaymentRequest() {}
    
    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
    public String getToIban() { return toIban; }
    public void setToIban(String toIban) { this.toIban = toIban; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public TransactionCategory getCategory() { return category; }
    public void setCategory(TransactionCategory category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}