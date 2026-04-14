package com.payment.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteOperationDTO {
    //private Long id;
    private Long accountId;
    private String name;
    private String recipientIban;
    private BigDecimal amount;
    private String category;
    private String description;
    //private String type;
    //private String recipientAccountName;
}