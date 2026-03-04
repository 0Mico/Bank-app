package com.bankapp.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankapp.payment.entity.FavoriteOperation;

@Repository
public interface FavoriteOperationRepository extends JpaRepository<FavoriteOperation, Long> {
    List<FavoriteOperation> findByUserId(Long userId);
    Optional<FavoriteOperation> findByCategory(String category);
}
