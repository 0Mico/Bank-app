package com.bankapp.common.dto;

import java.math.BigDecimal;

public class DepositDTO {
    private BigDecimal amount;

    public DepositDTO() {}
    public DepositDTO(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
