package com.common.model;

import com.common.enums.TransactionCategory;
import com.common.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionModel {
    private Long id;
    private Long userId;
    private Long accountId;
    private TransactionType type;
    private TransactionCategory category;
    private BigDecimal amount;
    private String description;
    private String referenceId;

    @Size(max = 34)
    private String counterpartyIban;
    
    private OffsetDateTime createdAt;
}
