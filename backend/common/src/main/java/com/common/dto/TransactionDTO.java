package com.common.dto;

import com.common.enums.TransactionCategory;
import com.common.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long userId;
    private Long accountId;
    private TransactionType type;
    private TransactionCategory category;
    private BigDecimal amount;
    private String description;
    private String referenceId;
    private String counterpartyIban;
}
