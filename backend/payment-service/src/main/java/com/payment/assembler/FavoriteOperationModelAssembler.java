package com.payment.assembler;

import java.util.List;

import org.springframework.stereotype.Component;

import com.common.interfaces.AccountServiceApi;
import com.common.dto.RecipientInfoDTO;
import com.payment.entity.FavoriteOperation;
import com.payment.mapper.FavoriteOperationModelMapper;
import com.payment.models.FavoriteOperationModel;

@Component
public class FavoriteOperationModelAssembler {

    private final FavoriteOperationModelMapper favoriteOperationModelMapper;
    private final AccountServiceApi accountServiceClient;

    public FavoriteOperationModelAssembler(FavoriteOperationModelMapper mapper, AccountServiceApi accountServiceClient) {
        this.favoriteOperationModelMapper = mapper;
        this.accountServiceClient = accountServiceClient;
    }

    public FavoriteOperationModel toModel(FavoriteOperation entity) {
        RecipientInfoDTO info = accountServiceClient.analyzeRecipient(entity.getAccountId(), entity.getRecipientIban());
        return favoriteOperationModelMapper.toModel(entity, info.type(), info.accountName());
    }

    public List<FavoriteOperationModel> toModels(List<FavoriteOperation> entities) {
        return entities.stream().map(this::toModel).toList();
    }
}
