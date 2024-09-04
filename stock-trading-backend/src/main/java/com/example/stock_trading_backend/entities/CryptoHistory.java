package com.example.stock_trading_backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "crypto_history")
public class CryptoHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "asset_id", nullable = false)
    private String assetId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "price_usd", nullable = false)
    private Double priceUsd;

    @Column(name = "volume_1hrs_usd")
    private Double volume_1hrsUsd;

    @Column(name = "volume_1day_usd")
    private Double volume_1dayUsd;

    @Column(name = "volume_1mth_usd")
    private Double volume_1mthUsd;
}