package com.payment.controller;

import com.payment.assembler.FavoriteOperationModelAssembler;
import com.payment.dto.FavoriteOperationDTO;
import com.payment.entity.FavoriteOperation;
import com.payment.models.FavoriteOperationModel;
import com.payment.service.FavoriteOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments/favorites")
public class FavoriteOperationController {

    private final FavoriteOperationService favoriteOperationService;
    private final FavoriteOperationModelAssembler favoriteOperationModelAssembler;

    public FavoriteOperationController(FavoriteOperationService favoriteOperationService, FavoriteOperationModelAssembler favoriteOperationModelAssembler) {
        this.favoriteOperationService = favoriteOperationService;
        this.favoriteOperationModelAssembler = favoriteOperationModelAssembler;
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<FavoriteOperationModel>> getFavoritesByAccountId(@PathVariable Long accountId) {
        List<FavoriteOperation> operations = favoriteOperationService.getFavoriteByAccountId(accountId);
        return ResponseEntity.ok(favoriteOperationModelAssembler.toModels(operations));
    }

    @PostMapping
    public ResponseEntity<FavoriteOperationModel> createFavorite(@RequestBody FavoriteOperationDTO dto) {
        FavoriteOperation operation = favoriteOperationService.createFavorite(dto);
        return ResponseEntity.ok(favoriteOperationModelAssembler.toModel(operation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        favoriteOperationService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}
