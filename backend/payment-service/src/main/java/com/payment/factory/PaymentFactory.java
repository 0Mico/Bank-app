package com.payment.factory;

import com.common.interfaces.EntityFactory;
import com.payment.dto.PaymentDTO;
import com.payment.entity.Payment;

public interface PaymentFactory extends EntityFactory<Payment, PaymentDTO> {
}
