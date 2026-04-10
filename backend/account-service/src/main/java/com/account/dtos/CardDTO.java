package com.account.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private Long id;
    private Long accountId;
    private String cardNumber;
    private LocalDate expiration;
    private boolean isBlocked;
}