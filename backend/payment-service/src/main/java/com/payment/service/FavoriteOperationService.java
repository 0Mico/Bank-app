package com.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.common.exception.ResourceNotFoundException;
import com.payment.dtos.FavoriteOperationDTO;
import com.payment.entity.FavoriteOperation;
import com.payment.repository.FavoriteOperationRepository;

@Service
public class FavoriteOperationService {

    private final FavoriteOperationRepository favOpRepo;

    public FavoriteOperationService(FavoriteOperationRepository favOpRepo) {
        this.favOpRepo = favOpRepo;
    }

    public List<FavoriteOperation> getFavoriteByAccountId(Long accountId) {
        return favOpRepo.findByAccountId(accountId);
    }

    public FavoriteOperation createFavorite(FavoriteOperationDTO dto) {
        FavoriteOperation op = new FavoriteOperation();
        op.setAccountId(dto.getAccountId());
        op.setName(dto.getName());
        op.setRecipientIban(dto.getRecipientIban());
        op.setAmount(dto.getAmount());
        op.setCategory(dto.getCategory());
        op.setDescription(dto.getDescription());
        return favOpRepo.save(op);
    }
    
    public void deleteFavorite(Long id) {
        if (!favOpRepo.existsById(id)) {
            throw new ResourceNotFoundException("Operation not found among favorites", id);
        }
        favOpRepo.deleteById(id);
    }
}