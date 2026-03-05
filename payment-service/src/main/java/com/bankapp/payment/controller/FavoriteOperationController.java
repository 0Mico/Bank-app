package com.bankapp.payment.controller;

import com.bankapp.common.dto.FavoriteOperationDTO;
import com.bankapp.payment.service.FavoriteOperationService;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteOperationDTO>> getFavoritesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteOperationService.getFavoriteByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<FavoriteOperationDTO> createFavorite(@RequestBody FavoriteOperationDTO dto) {
        return ResponseEntity.ok(favoriteOperationService.createFavorite(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        favoriteOperationService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}
