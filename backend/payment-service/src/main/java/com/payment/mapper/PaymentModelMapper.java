package com.payment.mapper;

import com.payment.dto.PaymentDTO;
import com.payment.entity.Payment;
import com.payment.models.PaymentModel;
import com.payment.models.PaymentView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentModelMapper {

    PaymentModel toModel(PaymentView view);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "COMPLETED")
    @Mapping(target = "category", source = "category", defaultValue = "TRANSFER")
    @Mapping(target = "description", expression = "java(dto.getDescription() != null && !dto.getDescription().trim().isEmpty() ? dto.getDescription() : \"Payment from \" + dto.getFromAccountIban())")
    @Mapping(target = "createdAt", ignore = true)
    Payment dtoToEntity(PaymentDTO dto);
}
