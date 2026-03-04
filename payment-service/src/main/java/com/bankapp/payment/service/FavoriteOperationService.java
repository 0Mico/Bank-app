package com.bankapp.payment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bankapp.common.dto.FavoriteOperationDTO;
import com.bankapp.common.exception.ResourceNotFoundException;
import com.bankapp.payment.entity.FavoriteOperation;
import com.bankapp.payment.repository.FavoriteOperationRepository;

@Service
public class FavoriteOperationService {

    private final FavoriteOperationRepository favOpRepo;

    public FavoriteOperationService(FavoriteOperationRepository favOpRepo) {
        this.favOpRepo = favOpRepo;
    }

    public List<FavoriteOperationDTO> getFavoriteByUserId(Long id) {
        List<FavoriteOperation> operations = favOpRepo.findByUserId(id);
        return operations.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public FavoriteOperationDTO createFavorite(FavoriteOperationDTO dto) {
        FavoriteOperation op = new FavoriteOperation();
        op.setUserId(dto.getUserId());
        op.setName(dto.getName());
        op.setRecipientIban(dto.getRecipientIban());
        op.setAmount(dto.getAmount());
        op.setCategory(dto.getCategory());
        op.setDescription(dto.getDescription());
        
        op = favOpRepo.save(op);
        return toDTO(op);
    }
    
    public void deleteFavorite(Long id) {
        if (!favOpRepo.existsById(id)) {
            throw new ResourceNotFoundException("FavoriteOperation", id);
        }
        favOpRepo.deleteById(id);
    }

    private FavoriteOperationDTO toDTO(FavoriteOperation favOp) {
        FavoriteOperationDTO dto = new FavoriteOperationDTO();
        dto.setId(favOp.getId());
        dto.setUserId(favOp.getUserId());
        dto.setName(favOp.getName());
        dto.setRecipientIban(favOp.getRecipientIban());
        dto.setAmount(favOp.getAmount());
        dto.setCategory(favOp.getCategory());
        dto.setDescription(favOp.getDescription());
        return dto;
    }
}
