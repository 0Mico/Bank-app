package com.payment.service.baseService;

import com.common.interfaces.BaseService;
import com.payment.dto.PaymentDTO;
import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;

public interface BasePaymentService extends BaseService<PaymentRepository, Payment, Long> {
    
    Payment processPayment(PaymentDTO dto);
}
