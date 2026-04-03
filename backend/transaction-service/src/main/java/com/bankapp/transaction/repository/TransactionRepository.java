package com.bankapp.transaction.repository;

import com.bankapp.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        List<Transaction> findByUserId(Long userId);

        @Query(value = "SELECT * FROM transactions WHERE " +
            "(:userId IS NULL OR user_id = :userId) AND " +
            "(:accountId IS NULL OR account_id = :accountId) AND " +
            "(:category IS NULL OR category = :category) AND " +
            "(:type IS NULL OR type = :type) AND " +
            "(:from IS NULL OR created_at >= CAST(:from AS timestamptz)) AND " +
            "(:to IS NULL OR created_at <= CAST(:to AS timestamptz)) " +
            "ORDER BY created_at DESC",
            nativeQuery = true)
        List<Transaction> findWithFilters(
                        @Param("userId") Long userId,
                        @Param("accountId") Long accountId,
                        @Param("category") String category,
                        @Param("type") String type,
                        @Param("from") String from,
                        @Param("to") String to);
}
