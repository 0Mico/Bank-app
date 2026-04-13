package com.payment.mapper;

import com.common.model.AccountModel;
import com.payment.entity.Payment;
import com.payment.models.PaymentView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentViewMapper {

    PaymentView toView(Payment payment, AccountModel fromAccount, AccountModel toAccount);
}
