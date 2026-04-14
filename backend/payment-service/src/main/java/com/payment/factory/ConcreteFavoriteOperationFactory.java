package com.payment.factory;

import com.payment.dto.FavoriteOperationDTO;
import com.payment.entity.FavoriteOperation;
import com.payment.mapper.FavoriteOperationModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcreteFavoriteOperationFactory implements FavoriteOperationFactory {

    private final FavoriteOperationModelMapper favoriteOperationModelMapper;

    @Override
    public FavoriteOperation create(FavoriteOperationDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Favorite Operation data cannot be null");
        }
        return favoriteOperationModelMapper.dtoToEntity(dto);
    }

}
