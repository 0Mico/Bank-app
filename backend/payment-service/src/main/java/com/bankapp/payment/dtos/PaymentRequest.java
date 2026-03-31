package com.bankapp.payment.dtos;

import java.math.BigDecimal;

import com.common.enums.TransactionCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PaymentRequest {

    @NotNull
    private Long fromAccountId;

    @NotBlank(message = "Recipient iban is required")
    @Size(max = 34)
    private String toIban;

    @NotNull(message = "Amount is required")
    @Positive(message = "ammount must be > 0")
    private BigDecimal amount;

    private TransactionCategory category;
    private String description;
}