package com.bankapp.transaction.repository;

import com.bankapp.common.enums.TransactionCategory;
import com.bankapp.common.enums.TransactionType;
import com.bankapp.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        List<Transaction> findByUserId(Long userId);

        @Query("SELECT t FROM Transaction t WHERE " +
                        "(:userId IS NULL OR t.userId = :userId) AND " +
                        "(:accountId IS NULL OR t.accountId = :accountId) AND " +
                        "(:category IS NULL OR t.category = :category) AND " +
                        "(:type IS NULL OR t.type = :type) AND " +
                        "(:from IS NULL OR t.createdAt >= :from) AND " +
                        "(:to IS NULL OR t.createdAt <= :to) " +
                        "ORDER BY t.createdAt DESC")

        List<Transaction> findWithFilters(
                        @Param("userId") Long userId,
                        @Param("accountId") Long accountId,
                        @Param("category") TransactionCategory category,
                        @Param("type") TransactionType type,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);
}
