package com.bankapp.payment.dtos;

import com.bankapp.common.enums.PaymentStatus;
import com.bankapp.common.enums.TransactionCategory;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private String toIban;
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private TransactionCategory category;
    private String description;
    private LocalDateTime createdAt;
}