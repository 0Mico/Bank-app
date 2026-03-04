package com.bankapp.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    private Long id;
    private Long userId;
    private String iban;
    private BigDecimal balance;
    private String currency;
    private String name;
    private LocalDateTime createdAt;

    public AccountDTO() {
    }

    public AccountDTO(Long id, Long userId, String iban, BigDecimal balance, String currency, String name,
            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
        this.name = name;
        this.createdAt = createdAt;
    }

    public AccountDTO(Long id, Long userId, BigDecimal balance, String currency, String name, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.name = name;
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

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
