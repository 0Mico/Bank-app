package com.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.account.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<List<Card>> findAllByAccount_Id(Long accountId);
}
