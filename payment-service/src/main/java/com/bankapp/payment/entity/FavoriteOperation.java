package com.bankapp.payment.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorite_operations")
public class FavoriteOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String recipientIban;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column()
    private String category;

    @Column(length = 1000)
    private String description;

    public FavoriteOperation(){}
    public FavoriteOperation(Long id, Long accountId, String name, String recipientIban, BigDecimal amount,
            String category, String description) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.recipientIban = recipientIban;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRecipientIban() { return recipientIban; }
    public void setRecipientIban(String recipientIban) { this.recipientIban = recipientIban; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}