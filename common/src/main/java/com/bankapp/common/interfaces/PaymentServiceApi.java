package com.bankapp.common.interfaces;

import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.dto.PaymentRequest;
import com.bankapp.common.dto.PaymentResponse;

import java.util.List;

public interface PaymentServiceApi {
    PaymentResponse processPayment(PaymentRequest payment);
    PaymentResponse getPaymentById(Long id);
    List<PaymentResponse> getPaymentsByUserId(Long userId);
    List<AccountDTO> getAccountsByUserId(Long userId);
}