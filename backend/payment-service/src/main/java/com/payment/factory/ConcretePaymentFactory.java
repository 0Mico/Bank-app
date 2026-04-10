package com.payment.factory;

import com.common.enums.PaymentStatus;
import com.common.enums.TransactionCategory;
import com.payment.dto.PaymentDTO;
import com.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class ConcretePaymentFactory implements PaymentFactory {

    @Override
    public Payment create(PaymentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Payment data cannot be null");
        }
        Payment payment = new Payment();
        payment.setFromAccountId(dto.getFromAccountId());
        payment.setToAccountId(dto.getToAccountId());
        payment.setAmount(dto.getAmount());
        
        String userDesc = dto.getDescription();
        boolean hasDesc = userDesc != null && !userDesc.trim().isEmpty();
        payment.setDescription(hasDesc ? userDesc : "Payment from " + dto.getFromAccountIban());
        
        payment.setCategory(dto.getCategory() != null ? dto.getCategory() : TransactionCategory.TRANSFER);
        payment.setStatus(PaymentStatus.COMPLETED);
        return payment;
    }
}
