package com.bankapp.payment.repository;

import com.bankapp.payment.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);
    Optional<Account> findByIban(String iban);
    Optional<List<Account>> findAllByUserId(Long userId);
}
