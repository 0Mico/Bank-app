package com.payment.service;

import java.util.List;

import com.payment.factory.FavoriteOperationFactory;
import org.springframework.stereotype.Service;

import com.common.exception.ResourceNotFoundException;
import com.payment.dto.FavoriteOperationDTO;
import com.payment.entity.FavoriteOperation;
import com.payment.repository.FavoriteOperationRepository;

@Service
public class FavoriteOperationService {

    private final FavoriteOperationRepository favOpRepo;
    private final FavoriteOperationFactory favOpFactory;

    public FavoriteOperationService(FavoriteOperationRepository favOpRepo, FavoriteOperationFactory favOpFactory) {
        this.favOpRepo = favOpRepo;
        this.favOpFactory = favOpFactory;
    }

    public List<FavoriteOperation> getFavoriteByAccountId(Long accountId) {
        return favOpRepo.findByAccountId(accountId);
    }

    public FavoriteOperation createFavorite(FavoriteOperationDTO dto) {
        FavoriteOperation op = favOpFactory.create(dto);
        return favOpRepo.save(op);
    }
    
    public void deleteFavorite(Long id) {
        if (!favOpRepo.existsById(id)) {
            throw new ResourceNotFoundException("Operation not found among favorites", id);
        }
        favOpRepo.deleteById(id);
    }
}