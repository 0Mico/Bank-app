package com.payment.controller;

import com.payment.dtos.FavoriteOperationDTO;
import com.payment.service.FavoriteOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments/favorites")
public class FavoriteOperationController {

    private final FavoriteOperationService favoriteOperationService;

    public FavoriteOperationController(FavoriteOperationService favoriteOperationService) {
        this.favoriteOperationService = favoriteOperationService;
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<FavoriteOperationDTO>> getFavoritesByAccountId(@PathVariable Long accountId) {
        List<FavoriteOperationDTO> favorites = favoriteOperationService.getFavoriteByAccountId(accountId);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping
    public ResponseEntity<FavoriteOperationDTO> createFavorite(@RequestBody FavoriteOperationDTO dto) {
        FavoriteOperationDTO favorite = favoriteOperationService.createFavorite(dto);
        return ResponseEntity.ok(favorite);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        favoriteOperationService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}
