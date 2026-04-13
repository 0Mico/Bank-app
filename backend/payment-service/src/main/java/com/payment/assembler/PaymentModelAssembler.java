package com.payment.assembler;

import com.common.model.AccountModel;
import com.common.interfaces.AccountServiceApi;
import com.payment.entity.Payment;
import com.payment.mapper.PaymentModelMapper;
import com.payment.mapper.PaymentViewMapper;
import com.payment.models.PaymentModel;
import com.payment.models.PaymentView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentModelAssembler {

    private final AccountServiceApi accountServiceClient;
    private final PaymentViewMapper paymentViewMapper;
    private final PaymentModelMapper paymentModelMapper;

    public PaymentModelAssembler(AccountServiceApi accountServiceClient, PaymentViewMapper paymentViewMapper, PaymentModelMapper paymentModelMapper) {
        this.accountServiceClient = accountServiceClient;
        this.paymentViewMapper = paymentViewMapper;
        this.paymentModelMapper = paymentModelMapper;
    }

    public PaymentModel toModel(Payment payment) {
        AccountModel fromAccount = accountServiceClient.getAccountById(payment.getFromAccountId());
        AccountModel toAccount = accountServiceClient.getAccountById(payment.getToAccountId());
        PaymentView view = paymentViewMapper.toView(payment, fromAccount, toAccount);
        return paymentModelMapper.toModel(view);
    }

    public List<PaymentModel> toModels(List<Payment> payments) {
        return payments.stream().map(this::toModel).toList();
    }
}
