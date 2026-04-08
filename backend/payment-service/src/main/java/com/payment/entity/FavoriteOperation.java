package com.payment.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favorite_operations")
@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 34)
    private String recipientIban;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column()
    private String category;

    @Column(length = 1000)
    private String description;
}