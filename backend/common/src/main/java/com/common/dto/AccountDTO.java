package com.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    
    private Long id;
    private Long userId;
    
    @Size(max = 34)
    private String iban;

    private BigDecimal balance;
    private String currency;
    private String name;
    private LocalDateTime createdAt;
}
