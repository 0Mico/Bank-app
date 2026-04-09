package com.payment.mapper;

import org.mapstruct.Mapper;

import com.payment.entity.FavoriteOperation;
import com.payment.models.FavoriteOperationModel;

@Mapper(componentModel = "spring")
public interface FavoriteOperationModelMapper {

    FavoriteOperationModel toModel(FavoriteOperation entity, String type, String recipientAccountName);
}
