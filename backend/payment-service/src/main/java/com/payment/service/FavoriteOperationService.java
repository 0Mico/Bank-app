package com.payment.service;

import java.util.List;

import com.payment.factory.FavoriteOperationFactory;
import org.springframework.stereotype.Service;

import com.common.exception.ResourceNotFoundException;
import com.payment.dto.FavoriteOperationDTO;
import com.payment.entity.FavoriteOperation;
import com.payment.repository.FavoriteOperationRepository;
import com.payment.service.baseService.BaseFavoriteOperationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteOperationService implements BaseFavoriteOperationService {

    private final FavoriteOperationRepository favOpRepo;
    private final FavoriteOperationFactory favOpFactory;

    @Override
    public FavoriteOperationRepository getRepository() {
        return this.favOpRepo;
    }

    @Override
    public List<FavoriteOperation> getFavoriteByAccountId(Long accountId) {
        return favOpRepo.findByAccountId(accountId);
    }

    @Override
    public FavoriteOperation createFavorite(FavoriteOperationDTO dto) {
        FavoriteOperation op = favOpFactory.create(dto);
        return favOpRepo.save(op);
    }
    
    @Override
    public void deleteFavorite(Long id) {
        if (!favOpRepo.existsById(id)) {
            throw new ResourceNotFoundException("Operation not found among favorites", id);
        }
        favOpRepo.deleteById(id);
    }
}