package com.bankapp.payment.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false, unique = true)
    private String cardNumber;

    @NotNull
    private Date expiration;

    @Column(nullable = false)
    private boolean isBlocked = false;

    public Card() {}
    public Card(Long id, String cardNumber, Date expiration, boolean isBlocked) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.expiration = expiration;
        this.isBlocked = isBlocked;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public Date getExpiration() { return expiration; }
    public void setExpiration(Date expiration) { this.expiration = expiration; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean isBlocked) { this.isBlocked = isBlocked; }

}