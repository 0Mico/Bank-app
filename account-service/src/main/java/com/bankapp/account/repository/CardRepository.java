package com.bankapp.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankapp.account.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<List<Card>> findAllByAccountId(Long accountId);
}
