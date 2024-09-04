package com.example.stock_trading_backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "cryptos")

public class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference("crypto-portfolio-cryptos")
    @OneToMany(mappedBy = "crypto", fetch = FetchType.LAZY)
    private List<PortfolioCrypto> portfolioCryptos = new ArrayList<>();

    @JsonManagedReference("crypto-transactions")
    @OneToMany(mappedBy = "crypto", fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    private String assetId;
    private String name;
    private boolean typeIsCrypto;
    private String dataQuoteStart;
    private String dataQuoteEnd;
    private String dataTradeStart;
    private String dataTradeEnd;
    private double volume1hrsUsd;
    private double volume1dayUsd;
    private double volume1mthUsd;
    private double priceUsd;

}

