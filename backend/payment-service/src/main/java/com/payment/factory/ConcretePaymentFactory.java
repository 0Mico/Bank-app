package com.payment.factory;

import com.payment.dto.PaymentDTO;
import com.payment.entity.Payment;
import com.payment.mapper.PaymentModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcretePaymentFactory implements PaymentFactory {

    private final PaymentModelMapper paymentModelMapper;

    @Override
    public Payment create(PaymentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Payment data cannot be null");
        }
        return paymentModelMapper.dtoToEntity(dto);
    }
}
