package com.bankapp.common.interfaces;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.dto.PaymentDTO;

import java.util.List;

public interface PaymentServiceApi {
    PaymentDTO processPayment(PaymentDTO payment);
    PaymentDTO getPaymentById(Long id);
    List<PaymentDTO> getPaymentsByUserId(Long userId);
    List<AccountDTO> getAccountsByUserId(Long userId);
}