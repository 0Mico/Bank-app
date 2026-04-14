package com.payment.mapper;

import com.payment.models.PaymentModel;
import com.payment.models.PaymentView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentModelMapper {

    PaymentModel toModel(PaymentView view);
}
