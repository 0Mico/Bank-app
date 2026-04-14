package com.payment.models;

import com.common.dto.AccountDTO;
import com.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentView {
    private Payment payment;
    private AccountDTO fromAccount;
    private AccountDTO toAccount;
}
