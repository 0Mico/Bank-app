package com.payment.factory;

import com.payment.dtos.FavoriteOperationDTO;
import com.payment.entity.FavoriteOperation;
import org.springframework.stereotype.Component;

@Component
public class ConcreteFavoriteOperationFactory implements FavoriteOperationFactory {

    @Override
    public FavoriteOperation create(FavoriteOperationDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Favorite Operation data cannot be null");
        }
        FavoriteOperation op = new FavoriteOperation();
        op.setAccountId(dto.getAccountId());
        op.setName(dto.getName());
        op.setRecipientIban(dto.getRecipientIban());
        op.setAmount(dto.getAmount());
        op.setCategory(dto.getCategory());
        op.setDescription(dto.getDescription());
        return op;
    }

}
