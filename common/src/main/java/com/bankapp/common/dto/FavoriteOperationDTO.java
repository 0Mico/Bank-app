package com.bankapp.common.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteOperationDTO {
    private Long id;
    private Long accountId;
    private String name;
    private String recipientIban;
    private BigDecimal amount;
    private String category;
    private String description;
    private String type;
    private String recipientAccountName;

    public FavoriteOperationDTO() {}
    public FavoriteOperationDTO(Long id, Long accountId, String name, String recipientIban, BigDecimal amount,
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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRecipientAccountName() { return recipientAccountName; }
    public void setRecipientAccountName(String recipientAccountName) { this.recipientAccountName = recipientAccountName; }
}