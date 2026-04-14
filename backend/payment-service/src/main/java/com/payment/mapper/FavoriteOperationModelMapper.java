package com.payment.mapper;

import com.payment.dto.FavoriteOperationDTO;
import com.payment.entity.FavoriteOperation;
import com.payment.models.FavoriteOperationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteOperationModelMapper {

    FavoriteOperationModel toModel(FavoriteOperation entity, String type, String recipientAccountName);

    @Mapping(target = "id", ignore = true)
    FavoriteOperation dtoToEntity(FavoriteOperationDTO dto);
}
