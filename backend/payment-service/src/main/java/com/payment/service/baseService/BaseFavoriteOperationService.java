package com.payment.service.baseService;

import java.util.List;

import com.common.interfaces.BaseService;
import com.payment.dto.FavoriteOperationDTO;
import com.payment.entity.FavoriteOperation;
import com.payment.repository.FavoriteOperationRepository;

public interface BaseFavoriteOperationService extends BaseService<FavoriteOperationRepository, FavoriteOperation, Long> {
    List<FavoriteOperation> getFavoriteByAccountId(Long accountId);
    FavoriteOperation createFavorite(FavoriteOperationDTO dto);
    void deleteFavorite(Long id);
}
