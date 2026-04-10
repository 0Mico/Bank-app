package com.payment.dto;

import java.math.BigDecimal;

import com.common.enums.TransactionCategory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentDTO {

    @NotNull
    private Long fromAccountId;
    
    @JsonIgnore
    private Long toAccountId;

    @JsonIgnore
    private String fromAccountIban;
    
    @NotBlank(message = "Recipient iban is required")
    @Size(max = 34)
    private String toIban;

    @NotNull(message = "Amount is required")
    @Positive(message = "ammount must be > 0")
    private BigDecimal amount;

    private TransactionCategory category;
    private String description;


}