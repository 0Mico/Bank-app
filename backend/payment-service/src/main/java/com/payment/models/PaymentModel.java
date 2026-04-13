package com.payment.models;

import com.common.model.AccountModel;
import com.payment.entity.Payment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentModel {
    private Payment payment;
    private AccountModel fromAccount;
    private AccountModel toAccount;
}