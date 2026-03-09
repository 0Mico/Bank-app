package com.bankapp.common.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDTO {
    private Long id;
    private Long accountId;
    private String cardNumber;
    private LocalDate expiration;
    private boolean isBlocked;

    public CardDTO() {}

    public CardDTO(Long id, Long accountId, String cardNumber, LocalDate expiration, boolean isBlocked) {
        this.id = id;
        this.accountId = accountId;
        this.cardNumber = cardNumber;
        this.expiration = expiration;
        this.isBlocked = isBlocked;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public LocalDate getExpiration() { return expiration; }
    public void setExpiration(LocalDate expiration) { this.expiration = expiration; }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean isBlocked) { this.isBlocked = isBlocked; }
}
