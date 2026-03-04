package com.bankapp.payment.repository;

import com.bankapp.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.fromAccountId IN " +
            "(SELECT a.id FROM Account a WHERE a.userId = :userId) OR " +
            "p.toAccountId IN (SELECT a.id FROM Account a WHERE a.userId = :userId) " +
            "ORDER BY p.createdAt DESC")
    List<Payment> findByUserId(@Param("userId") Long userId);
}
