package com.bankapp.payment.repository;

import com.bankapp.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByFromAccountIdInOrToAccountIdInOrderByCreatedAtDesc(
            List<Long> fromAccountIds, List<Long> toAccountIds);
}
