package com.example.stock_trading_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "transactions")

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("crypto-transactions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_id")
    private Crypto crypto;

    @JsonBackReference("portfolio-transactions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    private String cryptoName;

    private String cryptoAssetId;

    private TransactionType type;

    private Double price;

    private Integer quantity;

    private Double pricePerCrypto;

    private LocalDateTime transactionDate;

    public enum TransactionType {
        BUY("BUY"),
        SELL("SELL"),
        ;

        TransactionType(String type) {
        }
    }

}
