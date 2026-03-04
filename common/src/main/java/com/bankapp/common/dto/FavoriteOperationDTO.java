package com.bankapp.common.dto;

import java.math.BigDecimal;

public class FavoriteOperationDTO {
    private Long id;
    private Long userId;
    private String name;
    private String recipientIban;
    private BigDecimal amount;
    private String category;
    private String description;


    public FavoriteOperationDTO() {}
    public FavoriteOperationDTO(Long id, Long userId, String name, String recipientIban, BigDecimal amount,
            String category, String description) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.recipientIban = recipientIban;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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