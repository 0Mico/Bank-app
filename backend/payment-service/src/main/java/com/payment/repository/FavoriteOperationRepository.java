package com.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.entity.FavoriteOperation;

@Repository
public interface FavoriteOperationRepository extends JpaRepository<FavoriteOperation, Long> {
    List<FavoriteOperation> findByAccountId(Long accountId);
    Optional<FavoriteOperation> findByCategory(String category);
}
