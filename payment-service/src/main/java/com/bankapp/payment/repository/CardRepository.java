package com.bankapp.payment.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankapp.payment.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<ArrayList<Card>> findAllByAccountId(Long accountId);
}
