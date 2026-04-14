package com.payment.mapper;

import com.common.dto.AccountDTO;
import com.payment.entity.Payment;
import com.payment.models.PaymentView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentViewMapper {

    PaymentView toView(Payment payment, AccountDTO fromAccount, AccountDTO toAccount);
}
